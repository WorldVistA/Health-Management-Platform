<%@ page import="groovy.xml.StreamingMarkupBuilder; grails.converters.XML; groovy.xml.XmlUtil" contentType="text/html;charset=UTF-8" %>
<html xmln="">
<head>
  <title>Clinical Practice Environment API Documentation &raquo; ${resourceDescriptor.name}</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
         <g:render template="/layouts/api"/>
  <script type="text/javascript">
  	Ext.onReady(function(){
        var appbar = Ext.ComponentQuery.query('appbar')[0];
		appbar.addAppMenuItem({
		    	xtype: 'button',
		    	icon: '/images/skin/database_table.png',
		    	text: 'Back to API Docs', 
		    	target: '_self',
		    	href: "${createLink(class: 'list', action: 'index')}"
		});
  	});
  </script>
</head>
<body>
<div class="body">
  <h1>${resourceDescriptor.name}</h1>
  <div class="propertyList">
    <table>
      <tbody>
      <tr class="prop">
        <th valign="top" class="name">URI</th>
        <td valign="top" class="value"><tt>${resourceDescriptor.path}</tt></td>
      </tr>
      <tr class="prop">
        <th valign="top" class="name">Description</th>
        <td valign="top" class="value">${resourceDescriptor.description}</td>
      </tr>
      <tr class="prop">
        <th valign="top" class="name">Parameters</th>
        <td valign="top" class="value">
          <g:if test="${resourceDescriptor.getParams()}">
            <ul>
              <g:each in="${resourceDescriptor.getParams()}" var="paramElement">
                <li>
                  <g:if test="${paramElement.@required == 'true'}">
                    <b>required</b>
                  </g:if>
                  <g:else>
                    <i>optional</i>
                  </g:else>
                &mdash;
                  <tt>${paramElement.@name}</tt>
                &mdash;
                  <span>${paramElement.doc.@title}</span>
                  <g:if test="${paramElement.doc.text()}">
                    <ul>
                      <li>${paramElement.doc.text()}</li>
                    </ul>
                  </g:if>
                  <g:if test="${paramElement.option}">
                    <ul>
                      <g:each in="${paramElement.option}" var="optionElement">
                        <li>
                          <tt>${optionElement.@value}</tt> &mdash;
                          <g:if test="${optionElement.@mediaType}">Selects the representation of media type: <tt>${optionElement.@mediaType}</tt></g:if>
                        </li>
                      </g:each>
                    </ul>
                  </g:if>
                </li>
              </g:each>
            </ul>
          </g:if>
          <g:else>
            None.
          </g:else>
        </td>
      </tr>
      <tr class="prop">
        <th valign="top" class="name">HTTP Methods</th>
        <td valign="top" class="value">
          <ul>
            <g:each in="${resourceDescriptor.methods}" var="methodElement">
              <li>
                <tt>${methodElement.@name}</tt>
                <g:if test="${methodElement.doc}">
                  &mdash;
                  ${methodElement.doc.@title}
                </g:if>
              </li>
            </g:each>
          </ul>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
  <h2>Media Types</h2>
  <div class="propertyList">
    <table>
      <thead>
      <th>Response&nbsp;Format</th>
      <th>Requested via...</th>
      </thead>
      <tbody>
      <g:if test="${resourceDescriptor.representations.size() == 1}">
        <tr class="prop">
          <td valign="top" class="name"><tt>${resourceDescriptor.representations[0].@mediaType}</tt></td>
          <td valign="top" class="value">There is only one media type for this resource</td>
        </tr>
      </g:if>
      <g:else>
        <g:each in="${resourceDescriptor.representations}" var="representationElement">
          <tr class="prop">
            <td valign="top" class="name"><tt>${representationElement.@mediaType}</tt></td>
            <td valign="top" class="value">
              <p>Requested via one of the following:</p>
              <ul>
                <li><tt>${representationElement.@mediaType}</tt> in the HTTP Accept header</li>
                <li><tt>format=${mimeTypes[representationElement.@mediaType.toString()]}</tt> query parameter</li>
              </ul>
            </td>
          </tr>
        </g:each>
      </g:else>
      </tbody>
    </table>
  </div>
  <g:if test="${resourceDescriptor.usageExamples}">
    <h2>Usage Examples</h2>
    <g:each in="${resourceDescriptor.usageExamples}" var="usage">
      <p>${usage.description}:</p>
      <script type="syntaxhighlighter" class="brush: xml; gutter:false; smart-tabs:false"><![CDATA[${usage.example}]]></script>
    </g:each>
    </dl>
  </g:if>
  <h2>Example Responses</h2>
  <g:each in="${resourceDescriptor.representations.doc}" var="representationDocElement">
    <p>${representationDocElement.@title}</p>
    <script type="syntaxhighlighter" class="brush: ${representationDocElement.'..'.@mediaType == 'application/json' ? 'js' : 'xml'}"><![CDATA[${representationDocElement.text().trim()}]]></script>
  </g:each>
</div>
<script type="text/javascript">
  SyntaxHighlighter.defaults["class-name"] = "code-panel"
  SyntaxHighlighter.all()
</script>
</body>
</html>