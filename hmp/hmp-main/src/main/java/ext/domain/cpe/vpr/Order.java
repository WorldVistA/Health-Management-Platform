package EXT.DOMAIN.cpe.vpr;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.vpr.pom.AbstractPatientObject;
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Order extends AbstractPatientObject implements IPatientObject {
	/**
	 * For VistA -- localId is the identifier from the order file
	 */
	private String localId;
    /**
     * The facility where the encounter occurred
     *
     * @see "HITSP/C154 16.17 Facility ID"
     */
    private String facilityCode;
    /**
     * The facility where the encounter occurred
     *
     * @see "HITSP/C154 16.18 Facility Name"
     */
    private String facilityName;
	/**
	 * the location name of the order
	 */
	private String locationName;
	/**
	 * the location IEN of the order
	 */
	private String locationCode;
	/**
	 * the name of the item ordered from VistA the .01 of Orderable Item file
	 */
	private String name;
	/**
	 * the id of the item ordered from VistA the IEN of the Orderable Item file
	 */
	private String oiCode;

    private String oiName;

    private String oiPackageRef;
	/**
	 * The text of the order for medication orders the sig.
	 */
	private String content;
	/**
	 * The date the order was written.
	 */
	private PointInTime entered;
	/**
	 * The date the order was started to be acted on
	 */
	private PointInTime start;
	/**
	 * The final stop date of the order
	 */
	private PointInTime stop;
	/**
	 * The type of order from VistA from the display group file.
	 */
	private String displayGroup;
	/**
	 * The status of the order from VistA
	 */
//	private OrderStatus status;
	private String statusCode;
	private String statusName;
    private String statusVuid;

	private String providerUid;
    private String providerName;
    
    private LinkedHashSet<Order> children;

    public Set<Order> getChildren() {
		return children;
	}

	public Order(){
		super(null);
	}

	@JsonCreator
	public Order(Map<String, Object> vals) {
		super(vals);
	}

	public String getLocalId() {
		return localId;
	}

	public String getLocationName() {
		return locationName;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public String getName() {
		return name;
	}

	public String getOiCode() {
		return oiCode;
	}

    public String getOiName() {
		return oiName;
	}

    public String getOiPackageRef() {
		return oiPackageRef;
	}

	public String getContent() {
		return content;
	}

	public PointInTime getEntered() {
		return entered;
	}

	public PointInTime getStart() {
		return start;
	}

	public PointInTime getStop() {
		return stop;
	}

	public String getDisplayGroup() {
		return displayGroup;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public String getStatusName() {
		return statusName;
	}

    public String getStatusVuid() {
		return statusVuid;
	}

	public String getProviderUid() {
		return providerUid;
	}

    public String getProviderName() {
		return providerName;
	}

	public String getSummary() {
		return content;
	}

    public String getFacilityCode() {
        return facilityCode;
    }

    public String getFacilityName() {
        return facilityName;
    }

	public String getKind() {
		return "Order";
	}

	public List getTaggers() {
		// if (uid)
		// return manualFlush { Tagger.findAllByUrl(uid) }
		// else
		// return []
		return null;
		// TODO - fix this
	}

//    @JsonIgnore
//    public void loadLinkData(IGenericPatientObjectDAO dao) {
//    	Set<Order> newKids = new HashSet<Order>();
//    	if(children!=null)
//    	{	
//    		for(Order ord: children)
//    		{
//    			newKids.add(dao.findByUID(Order.class, ord.uid));
//    		}
//        	children = newKids;
//    	}
//    }
}
