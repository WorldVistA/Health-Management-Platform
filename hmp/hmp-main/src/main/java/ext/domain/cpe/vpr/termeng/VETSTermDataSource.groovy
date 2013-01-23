package org.osehra.cpe.vpr.termeng;

import java.util.List;
import java.util.Map;
import java.util.Set;

import groovyx.net.http.*

public class VETSTermDataSource implements ITermDataSource {
    // TODO: turn this into an interface/abstractBaseClass soon
    def httpBuilder;

    /*
        This could hold all the data for code systems in the format:

        codeSystemID.meta = <metadata map>
            Where <metadata map> is: [name, version, description, etc.]
        codeSystemID.items = <item map> (null until loaded)
            Where <item map> is:



     */
    def List codeSystems;
    def Map codeSystemIdx;
    def List mapSets;
    def Map mapSetIdx;


    public VETSTermDataSource() {
        this("http://islvsswls2.fo-slc.DOMAIN.EXT:9205/")
    }

    public VETSTermDataSource(String restHostURL) {
    }

    protected void init() {
        // only reinitalize if needed, this should be invoked before every public method.
        if (!codeSystems) {
            load()
        }
    }

    public reset() {
        codeSystemIdx = null
        mapSetIdx = null
        codeSystems = null;
        mapSets = null;
    }

    protected void load() {
        // TODO: What should be eagarly loaded vs lazy loaded?
        // TODO: use a syncronize block to make it thread safe for reloading

        // reset and initalize
        reset();

        // load code system list and index it
        codeSystemIdx = [:]
        codeSystems = loadCodeSystemList();
        codeSystems.each {
            // also eagerly get the details
            it.meta = loadCodeSystemDetails(it.VUID)
            codeSystemIdx.put(it.VUID, it)
        }

        // load mapping list and index it
        mapSetIdx = [:]
        mapSets = loadMapSetList();
        mapSets.each {
            mapSetIdx.put(it.meta.VUID, it)
            mapSetIdx.put("$it.meta.SourceCodeSystemVUID:$it.meta.TargetCodeSystemVUID".toString(), it)
        }
    }

    public List loadMapSetList() {
        // load the metadata for all the lists, but the items will be null
        // and loaded on demand (lazy loading)
        def ret = []

        httpBuilder.request(Method.GET, ContentType.XML) {
            uri.path = "/sts.webservice/services/ct/ListMapSets"
            response.success = {resp, xml ->

                xml.MapSetDetails.each {
                    def tmp = [
                        items: [],
                        meta: [
                            VUID: it.VUID.text(),
                            VersionName: it.VersionName.text(),
                            PreferredDesignationName: it.PreferredDesignationName.text(),
                            EntryCount: it.NumberOfMapEntries.text(),
                            SourceCodeSystemVUID: it.SourceCodeSystemVUID.text(),
                            TargetCodeSystemVUID: it.TargetCodeSystemVUID.text()
                        ]
                    ]

                    // TODO: could multiple key's map to one map? VUID, name, etc?
                    // TODO: or maybe this could be tmp[fromCode][toCode].meta in addition to tmp[VUID].meta
                    ret.add(tmp)
                }
            }
        }

        return ret;
    }

    public List loadCodeSystemList() {
        def ret = []
        httpBuilder.request(Method.GET, ContentType.XML) {
            uri.path = "/sts.webservice/services/ct/ListCodeSystems"
            response.success = {resp, xml ->
                xml.children().each {
                    def tmp = [
                        items: [],
                        meta: [],
                        Name: it.Name.text(),
                        VUID: it.VUID.text(),
                        VersionName: it.versionNames.versionName[0].text()
                    ]

                    ret.add(tmp)
                }
            }
        }
        return ret;
    }

    /**
     * This method will be called lazily if/when the map entries are needed
     * @param mapSetID
     * @return
     */
    public loadMapSetItems(String mapSetID, int pageNum=1, String codeSystemVersion='current') {
        def ret = [:];
        // TODO: It seems like the source can be mapped to multiple targets?
        // TODO: their can be multiple mappings to one code (Systolic/Diastolic BP)

        httpBuilder.request(Method.GET, ContentType.XML) {
            // Example URL: http://islvsswls2.fo-slc.DOMAIN.EXT:9205/sts.webservice/services/ct/ListMapEntries/4712465/current
            uri.path = "/sts.webservice/services/ct/ListMapEntries/$mapSetID/$codeSystemVersion"
            response.success = {resp, xml ->
                xml.mapEntryDetailTransfers.each {
                    def tmp = [
                        VUID: it.MapEntryVUID.text(),
                        SourceConceptCode: it.SourceConceptCode.text(),
                        SourceConceptPreferredDesignationCode: it.SourceConceptPreferredDesignationCode.text(),
                        SourceConceptPreferredDesignationName: it.SourceConceptPreferredDesignationName.text(),
                        TargetConceptCode: it.TargetConceptCode.text(),
                        TargetConceptPreferredDesignationName: it.TargetConceptPreferredDesignationName.text(),
                        MapEntryOrder: it.MapEntryOrder.text(),
                        MapEntryStatus: it.MapEntryStatus.text()
                    ]

                    ret.put(tmp.SourceConceptCode, tmp);
                }
            }
        }

        // If more than 1000 results, get the rest of the pages.
        if (ret.size() >= 1000) {
            ret.putAll(loadMapSetItems(mapSetID, pageNum+1));
        }

        return ret;
    }

    public loadCodeSystemDetails(String codeSystemID, String codeSystemVersion='current') {
        def ret = [:]
        httpBuilder.request(Method.GET, ContentType.XML) {
            // example URL: http://islvsswls2.fo-slc.DOMAIN.EXT:9205/sts.webservice/services/ct/ReturnCodeSystemDetails/4516261/current
            uri.path = "/sts.webservice/services/ct/ReturnCodeSystemDetails/$codeSystemID/$codeSystemVersion"
            response.success = {resp, xml ->
                ret = [
                    VUID: xml.VUID.text(),
                    Name: xml.Name.text(),
                    Description: xml.Description.text(),
                    VersionName: xml.Version[0].Name.text(),
                    VersionDesc: xml.Version[0].Description.text(),
                    VersionConceptCount: xml.Version[0].ConceptCount.text()
                ]
            }
        }

        return ret
    }

    public loadCodeSystemConcepts(String codeSystemID, String codeSystemVersion='current', int pageNum=1, int pageSize=1000) {
        println("Loading concepts $pageNum/$pageSize")

        def ret = [:]

        httpBuilder.request(Method.GET, ContentType.XML) {
            uri.path = "/sts.webservice/services/ct/ListCodeSystemConcepts/$codeSystemID/$codeSystemVersion"
            uri.query = [pageSize: pageSize, pageNumber: pageNum]

            response.success = { resp, xml ->

                xml.children().each {

                    def tmp =  [
                       Code: it.Code.text()
                    ]

                    ret.put(tmp.Code, tmp)
                }
            }
        }

        // more results exist, get the next page
        if (ret.size() >= pageSize) {
            ret.putAll(loadCodeSystemConcepts(codeSystemID, codeSystemVersion, pageNum+1, pageSize))
        }


        return ret;
    }

    public loadConceptRelationships(String code, String codeSystemID, String codeSystemVersion='current') {

        // sample URL: http://islvsswls2.fo-slc.DOMAIN.EXT:9205/sts.webservice/services/ct/ListAssociations?VUID=4522046&VersionName=current&SourceConceptCode=414225004
        def ret = []
        httpBuilder.request(Method.GET, ContentType.XML) {
            uri.path = "/sts.webservice/services/ct/ListAssociations"
            uri.query = [VUID: codeSystemID, VersionName: codeSystemVersion, SourceConceptCode: code]

            response.success = { resp, xml ->

                xml.children().each() {
                    def tmp = [
                            SourceConceptCode: it.SourceConceptCode.text(),
                            TargetConceptCode: it.TargetConceptCode.text(),
                            SourceDesignationName: it.SourceDesignationName.text(),
                            TargetDesignationName: it.TargetDesignationName.text(),
                            RelationshipTypeName: it.RelationshipTypeName.text()
                    ]

                    ret.add(tmp)
                }
            }
        }

        return ret;
    }


    /*
        This builds on top of loadConceptRelationships() to compute all of the parent/ancestor relationships
        by traversing up the tree (ignoring everything but ISA relationships)
     */
    public loadAncestorRelationships(String code, String codeSystemID, String codeSystemVersion='current', int recurseLimit=3) {
        println ("Loading parent/ancestors of: $code")
        // get the first set of ISA's (representing the parent concepts)
        def ret = loadConceptRelationships(code, codeSystemID, codeSystemVersion)

        // recursivley execute (only on ISA's)
        def ret2 = []
        ret.each {
            if (it.RelationshipTypeName.equals('ISA')) {
               println("Found parent: $it.TargetConceptCode")
               if (recurseLimit > 0) {
                  ret2.addAll(loadAncestorRelationships(it.TargetConceptCode, codeSystemID, codeSystemVersion, recurseLimit-1))
               } else {
                   println ("Recurse limit reached")
               }

            }
        }

        // merge results
        ret.addAll(ret2)

        return ret;
    }

    ///////////////////////////////////////////////
    // Interface Methods Implementation
    ///////////////////////////////////////////////

    /**
     * The code system list is the combination of the known map sets and the known code systems
     */
    public Set<String> getCodeSystemList() {
        init()
        // TODO: cache this as a field variable
        def ret = [:]
        mapSets.each { key, value -> ret[key]=''}
        codeSystems.each { key, value -> ret[key]=''}
        return ret.keySet();
    }
	
	@Override
	public Map<String, Object> getCodeSystemMap() {
		return [:]
	}
	
	@Override
	public List<String> search(String text) {
		return null; // not implemented
	}

    private String normalizeCodeSystem(String id) {
        // this is so users can pass in a VUID, 'SMN', OIDs, etc. values
        return id;
    }

    /**
     * TODO: check by more than just the ID?
     *
     * @param id
     * @return
     */
    public boolean isValidCodeSystem(String id) {
        return getCodeSystemList().containsKey(normalizeCodeSystem(id));
    }

    /*
        Returns the mapping set by code (code could be multiple things, VUID, etc.)
        This will also lazy load the map set if it hasn't been loaded yet.
     */
    public findMapSet(String from, String to) {
        init()
        def set = mapSetIdx.get("$from:$to".toString());
        if (!set && !to) {
           set = mapSetIdx.get(from);
        }
        if (!set) {return null}

        // lazy load needed?
        if (!set.items || !set.items.size()) {
           // TODO: Multi-threaded issue here.
           set.items = loadMapSetItems(set.meta.VUID)
        }

        return set
    }

    public Map<String, String> getMappings(String id) {
        def set = findMapSet(id, null);

        def ret = [:]
        set.items.each {
            ret.put(it.key, it.value)
        }
        return ret;
    }

    public String getMapping(String code, String from, String to) {
        // TODO: Could there be a case where the mappings need to be looked up from the codesystem not the mappings?

        def set = findMapSet(from, to);
        if (!set) return null
        def item = set.items[code];

        // TODO: what if this is an array (multiple mappings), not a single item?
        return item;
    }

	@Override
	public Set<String> getAncestorSet(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(String urn) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> getEquivalentSet(String urn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getParentSet(String urn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription(String urn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getConceptData(String urn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getRelMap(String urn) {
		// TODO Auto-generated method stub
		return null;
	}
}
