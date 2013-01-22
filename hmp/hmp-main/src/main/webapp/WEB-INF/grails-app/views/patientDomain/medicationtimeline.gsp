  <script type="text/javascript">
      var injectScriptElement = function(id, url, onLoad, onError, scope) {
          var script = document.createElement('script'),
              documentHead = typeof document !== 'undefined' && (document.head || document.getElementsByTagName('head')[0]),
              cleanupScriptElement = function(script) {
                   script.id = id;
                   script.onload = null;
                   script.onreadystatechange = null;
                   script.onerror = null;

                   return this;
              },
              onLoadFn = function() {
                  cleanupScriptElement(script);
                  onLoad.call(scope);
              },
              onErrorFn = function() {
                  cleanupScriptElement(script);
                  onError.call(scope);
              };

          // if the script is already loaded, don't load it again
          if (document.getElementById(id) !== null) {
               onLoadFn();
               return;
          }

          script.type = 'text/javascript';
          script.src = url;
          script.onload = onLoadFn;
          script.onerror = onErrorFn;
          script.onreadystatechange = function() {
              if (this.readyState === 'loaded' || this.readyState === 'complete') {
                  onLoadFn();
              }
          };
          documentHead.appendChild(script);
          return script;
      }
  	
      var addStyleSheet = function(id, url) {
          var ss = document.createElement("link");
          ss.setAttribute("rel", "stylesheet");
          ss.setAttribute("type", "text/css");
          ss.setAttribute("id", id);
          ss.setAttribute("href", url);
          document.getElementsByTagName("head")[0].appendChild(ss);
      };
            

  	<%
		out.println("var data = " + renderer.renderToString(viewdef, params));
	%>
	
	var params = {
		groupMeds: <%= params.group_meds ? true : false %>,
		showIcons: <%= params.show_icons ? true : false %>
	};

	var parseDate = function(str) {
		// input is: 20120113134600.000 or 20120113 
		if (!str) {
			return null
		}
		
		var ret = null;
		if (str.length == 8) {
			ret = Ext.Date.parse(str, "Ymd");
	    } else if (str.length == 12) {
	    	ret = Ext.Date.parse(str, "YmdHi");
		} else if (str.length >= 14) {
			str = str.substr(0, 14);
			ret = Ext.Date.parse(str, "YmdHis")
		}
		return ret;
	}
	
	var translateMed = function(obj) {
		var fields = obj.summary.split(' ');
		var ret = {
			start: parseDate(obj.overallStart),
			end: parseDate(obj.overallStop),
			content: '<span title="' + obj.summary + '">' + obj.ingredientName + "</span>"
		}
		if (params.groupMeds) {
			ret.group = obj.drugClassName + '<span id=' + obj.uid + '></span>'
		}
		
		return ret;
	}
	var translateEvt = function(obj) {
		var ret = {
			start: parseDate(obj.datetime),
			content: obj.summary
		}
		
		return ret;
	}
	var translateEnc = function(obj) {
		var ret = {
			start: parseDate(obj.dateTime),
			end: parseDate(obj.dischargeDateTime),
			content: obj.kind + ": " + obj.typeName
		}
		if (params.showIcons) {
			var icon = "/images/icons/user_suit.png";
			if (obj.kind == 'Admission')  {
				icon = "/images/icons/building.png";
			}
			ret.content = '<img src="' + icon + '" title="' + ret.content + '"/>';
		}
		return ret;
	}
	
	var data1 = [];
	for (var i in data.data) {
		data1[i] = translateMed(data.data[i]);
	}

	var error = function() {
	    console.log('error occurred');
	}

  	var init = function() {
		var conf1 = {
			//height: '100%'
			//width: '100%',
			//animate: false
			style: 'dot',
			axisOnTop: true,
			showMajorLabels: true
		}

      function onrangechange1() {
        var range = timeline.getVisibleChartRange();
        timeline2.setVisibleChartRange(range.start, range.end);
      }
            
      function onrangechange2() {
        var range = timeline2.getVisibleChartRange();
        timeline.setVisibleChartRange(range.start, range.end);
      }		

		var timeline = new links.Timeline(document.getElementById('timelineContent1'));
		links.events.addListener(timeline, 'rangechange', onrangechange1);
		timeline.draw(data1, conf1);

		var conf2 = {
				//height: '100%'
				//width: '100%',
				//animate: false
				style: 'dot',
				axisOnTop: false,
				showMajorLabels: true
			}

		var timeline2 = new links.Timeline(document.getElementById('timelineContent2'));
		links.events.addListener(timeline2, 'rangechange', onrangechange2);
		Ext.Ajax.request({
			url: '/vpr/view/EXT.DOMAIN.cpe.vpr.queryeng.RecentViewDef?pid=<%= params.pid%>',
			success: function(resp) {
				var data = Ext.JSON.decode(resp.responseText).data;
				var newdata = [];
				for (var i in data) {
					if (data[i].domain != 'medication') {
						timeline2.addItem(translateEvt(data[i]));
					}
				}
				timeline2.redraw();
			}
		});
		Ext.Ajax.request({
			url: '/vpr/view/EXT.DOMAIN.cpe.vpr.queryeng.EncounterViewDef?pid=<%= params.pid%>',
			success: function(resp) {
				var data = Ext.JSON.decode(resp.responseText).data;
				var newdata = [];
				for (var i in data) {
					timeline2.addItem(translateEnc(data[i]));
				}
				timeline2.redraw();
			}
		});
		timeline2.draw([], conf2);
		onrangechange1()
	}

    addStyleSheet('timelineCSS', '/lib/timeline/timeline.css');
	injectScriptElement('timelineScript', '/lib/timeline/timeline.js?date=' + new Date().getTime(), init, error, this);
</script>
<style>
	div.timeline-event-content	{
		margin: 0px;
		white-space: nowrap;
		overflow: hidden;
		font-size: 80%	
	}
	div.timeline-groups-text {
		color: #4D4D4D;
		padding-left: 10px;
		padding-right: 10px;
		font-size: 80%;	
	}
</style>
<div id="timelineContent1"></div>
<div id="timelineContent2"></div>
