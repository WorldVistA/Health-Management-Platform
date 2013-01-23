/*
* Copyright 2010 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* ----------------------------------------------------------------------------
* Original Author: Mike Brevoort, http://mike.brevoort.com
* Project sponsored by:
*     Avalon Consulting LLC - http://avalonconsult.com
*     Patheos.com - http://patheos.com
* ----------------------------------------------------------------------------
* 
* Modified by Solomon Blaz for the Department of Veterans Affairs
*
* ----------------------------------------------------------------------------
*/

package org.osehra.cpe.vpr.dao.solr

class Solr1 implements Comparable {
    static solr = {
        constant name:'title', value:'myobjecttitle'
    }

    Long id
    Long version

    String aString
    int anInt
    long aLong
    Date aDate
    float aFloat

    int compareTo(Object t) {
        return id.compareTo(t?.id)
    }


}
