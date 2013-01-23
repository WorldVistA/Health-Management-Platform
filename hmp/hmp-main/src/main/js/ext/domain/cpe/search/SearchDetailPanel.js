Ext.define("org.osehra.cpe.search.SearchDetailPanel", {
    extend: 'Ext.panel.Panel',
    mixins: {
        patientaware: 'org.osehra.hmp.PatientAware'
    },
    alias: 'widget.searchdetail',
    config: {
        titleProperty: 'summary',
        detailItem: null,
        decorateFn: null
    },
    title: "Detail",
    bodyPadding: 4,
    autoScroll: true,
    html: 'No item selected.',
    layout: 'fit',
    loader: {
    	loadMask: true,
        ajaxOptions: {
            method: 'GET'
        },
        params: {
            format: 'html'
        },               
        listeners: {
            beforeload: function(loader, op, eopts) {
            	var target = loader.getTarget();
                target.removeAll();
                target.update("");
                
                op.skipErrors = true;
                if(target.body){
                	target.body.unmask();
                }
            }          
        },        
        renderer: function(loader, response, active) {
        	var detail = loader.getTarget();        	
            var text = response.responseText;
            if (Ext.isFunction(detail.getDecorateFn())) {
            	detail.getDecorateFn();
                text = detail.getDecorateFn().call(this, text);
            }
            detail.update(text);
        },
		failure: function(loader, response) {
			var target = loader.getTarget();
			//this looks like bug in JS when failure called if success is undefined for loader
			//look in response status 
			if(response.status!=200){
				target.update("");
				if(target.body){
					target.body.mask("Component Received an Error. Try Reloading.");
				}
			}
		}
    },
    listeners: {
        beforepatientchange: function(cmp, pid) {
            this.update('No item selected.');
        },
        patientchange: function(pid) {
            this.pid = pid;
            this.removeAll();
        },
        patientupdate: function(domains) {
//            console.log('In patient update DetailPanel');
//            console.log(this);
        }

    },
    constructor: function(config) {
        this.initConfig(config);
        return this.callParent(arguments);
    },
    onBoxReady:function() {
        this.initPatientContext();
        this.callParent(arguments);
    },
    updateChartData: function(chartData) {
    	var cfg = {
			xtype: 'chartpanel',
			legend: {position: 'top'},
			width: this.width,
			height: this.height,
			id: 'searchDetailChartPanelId'
    	};
    	this.removeAll();
    	var chartPanel = this.add(cfg);
    	var chartCfg = {
			xAxis: {
				name: 'Observation Time',
				tickPixelInterval: 150,
	            dateTimeLabelFormats: {
	                second: '%d-%b-%y<br/>%H:%M:%S',
	                minute: '%d-%b-%y<br/>%H:%M',
	                hour: '%d-%b-%y<br/>%H:%M',
	                day: '%d-%b<br/>%Y',
	                week: '%d-%b<br/>%Y',
	                month: '%y-%b',
	                year: '%Y'
	            },
        		labels: {rotation: 0, align: 'left'}
			},
			tooltip: {
				enabled: true,
	            shared: false,
	            useHTML: true,
				formatter: function() {
						return '<b>'+ this.series.name +'</b><br/>'+
						Highcharts.dateFormat('%A,%b %e,%Y', this.x) +': '+ this.y;
				}
			},
    		plotOptions: {
        		line: {
        			dataLabels: {
        				enabled: true, 
        				backgroundColor: 'rgba(255,0,0,0.7)',
	        			color: 'rgba(255,255,255,0.7)',
        				formatter: function(){
							if(this.point.interpreted) {
								return this.y + ' ' + this.point.interpreted;
							}
        				},
        			}
        		}
	        }
    	};
    	chartPanel.updateChart(chartData, chartCfg);
    },
    applyDetailItem: function(newItem) {
    	var me = this;
        if (newItem) {
            var title = newItem.get(this.getTitleProperty());
            if (title) {
                this.setTitle(title);
            }

            var uid = newItem.get('uid');
            if (uid) {
                var domain = newItem.get('type');
                if (domain == 'result' || domain == 'vital_sign' || uid.indexOf("lab") != -1 || uid.indexOf("vs") != -1) {
                    this.getLoader().load({
                        url: '/vpr/trend/' + encodeURIComponent(uid),
                        renderer: 'data',
                        params: {
                            format: 'json'
                        },
                        success: function(loader, response) {
                        	var jsonResult = Ext.JSON.decode(loader.responseText);
                        	if(!jsonResult) return
                        	
                        	var chartData = [];
                        	chartData[0] = jsonResult.data;
                        	chartData[0].data = chartData[0].items;
                        	chartData[0].items=null;
                        	me.updateChartData(chartData);
                        },
                    });
                } else {
                    this.getLoader().load({
                        url: '/vpr/detail/' + encodeURIComponent(uid)
                    });
                }
            } else {
                this.update('Unable to fetch detail for this item.');
            }
        }
    }
});
