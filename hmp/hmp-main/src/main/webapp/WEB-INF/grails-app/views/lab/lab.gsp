<%@page import="java.awt.image.renderable.ParameterBlock"%>
<%@ page import="EXT.DOMAIN.cpe.vpr.queryeng.*" %>
<html>
	<head>
	<!-- CSS goes in the document HEAD or added to your external stylesheet -->
		<style type="text/css">
		table.gridtable {
			font-family: verdana,arial,sans-serif;
			font-size:11px;
			color:#333333;
			border-width: 1px;
			border-color: #666666;
			border-collapse: collapse;
		}
		table.gridtable th {
			border-width: 1px;
			padding: 8px;
			border-style: solid;
			border-color: #666666;
			background-color: #dedede;
		}
		table.gridtable td {
			border-width: 1px;
			padding: 8px;
			border-style: solid;
			border-color: #666666;
			background-color: #ffffff;
		}
		</style>
	</head>
	<body>
<%  
		Collection<Map<String, Object>> rows = new ArrayList(query.getRows());
		Collections.sort(rows, new Comparator<Map<String, Object>>(){
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return o2.get("resulted").compareTo(o1.get("resulted"));
			}
		});
		Map<String, Map<String, Object>> displayMap = new LinkedHashMap<String, Map<String, Object>>(); 
		for (Map row: rows) {
			String accessionId = row.get("accessionId");
			Map<String, Object> displayRow = displayMap.get(accessionId); 
			if(displayRow == null) {
				displayRow = new HashMap<String, Object>();
				displayMap.put(accessionId, displayRow);
			}
			displayRow.put(row.get("name"), row);
		}	
		
		//CHEM Panel
		out.println("<table class='gridtable'>");
		out.println("<tr>");
		out.println("<th><b>CHEM</b></th>");
		out.println("<th>Gluc</th>");
		out.println("<th>Na</th>");
		out.println("<th>K</th>");
		out.println("<th>Cl</th>");
		out.println("<th>CO2</th>");
		out.println("<th>Create</th>");
		out.println("<th>BUN</th>");
		out.println("<th>Ca</th>");
		out.println("</tr>");
		
		Set chemGroup = new HashSet();
		chemGroup.add("GLUCOSE");
		chemGroup.add("SODIUM");
		chemGroup.add("POTASSIUM");
		chemGroup.add("CHLORIDE");
		chemGroup.add("CO2");
		chemGroup.add("CREATININE");
		chemGroup.add("UREA NITROGEN");
		chemGroup.add("CALCIUM");
		
		for (String accessionId: displayMap.keySet()) {
			Map<String, Object> displayRow = displayMap.get(accessionId);
			out.println("<tr>");
			if(displayRow != null) {
				int count = 0;
				int resultCount = 0;
				
				for (String name: displayRow.keySet()) {
					if(chemGroup.contains(name)) {
						resultCount++;
					}
			    }
				//if(resultCount == 0) {
				if(resultCount < 2) {
					continue;
				}
				for (String name: displayRow.keySet()) {
					Map<String, Object> displayResult = displayRow.get(name);
					//Map<String, Object> resultMap = displayResult.get(name);
					if(count == 0) {
						out.println("<td>"+ hmp.formatDate(date:displayResult.get("resulted")) +"<br>accessionId=" + accessionId + 
						"<br># of Results="+ displayRow.size()+"</td>");
						count++;
					}
			   }
				
				if(displayRow.get("GLUCOSE")!= null) {
					Map<String, Object> displayResult = displayRow.get("GLUCOSE");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("SODIUM")!= null) {
					Map<String, Object> displayResult = displayRow.get("SODIUM");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("POTASSIUM")!= null) {
					Map<String, Object> displayResult = displayRow.get("POTASSIUM");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("CHLORIDE")!= null) {
					Map<String, Object> displayResult = displayRow.get("CHLORIDE");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("CO2")!= null) {
					Map<String, Object> displayResult = displayRow.get("CO2");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("CREATININE")!= null) {
					Map<String, Object> displayResult = displayRow.get("CREATININE");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}

				if(displayRow.get("UREA NITROGEN")!= null) {
					Map<String, Object> displayResult = displayRow.get("UREA NITROGEN");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("CALCIUM")!= null) {
					Map<String, Object> displayResult = displayRow.get("CALCIUM");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
			}
			out.println("</tr>");
		}
		out.println("</table>");
%>
<br>
<% 		
		//Liver Function Panel
		out.println("<table class='gridtable'>");
		out.println("<tr>");
		out.println("<th><b>LFT</b></th>");
		out.println("<th>PROTEITN TTL</th>");
		out.println("<th>ALBUMIN</th>");
		out.println("<th>BILIRUBIN,TTL</th>");
		out.println("<th>BILIRUBIN,DIR</th>");
		out.println("<th>ALK PHOS</th>");
		out.println("<th>AST</th>");
		out.println("<th>ALT</th>");
		out.println("</tr>");
		
		Set liverFunctionGroup = new HashSet();
		liverFunctionGroup.add("PROTEIN,TOTAL");
		liverFunctionGroup.add("ALBUMIN");
		liverFunctionGroup.add("TOT. BILIRUBIN");
		liverFunctionGroup.add("DIR. BILIRUBIN");
		liverFunctionGroup.add("ALKALINE PHOSPHATASE");
		liverFunctionGroup.add("AST");
		liverFunctionGroup.add("ALT");
		
		for (String accessionId: displayMap.keySet()) {
			Map<String, Object> displayRow = displayMap.get(accessionId);
			out.println("<tr>");
			if(displayRow != null) {
				int count = 0;
				int resultCount = 0;
				
				for (String name: displayRow.keySet()) {
					if(liverFunctionGroup.contains(name)) {
						resultCount++;
					}
			    }
			    
				//if(resultCount == 0) {
				if(resultCount < 2) {
					continue;
				}
				
				for (String name: displayRow.keySet()) {
					Map<String, Object> displayResult = displayRow.get(name);
					//Map<String, Object> resultMap = displayResult.get(name);
					if(count == 0) {
						out.println("<td>"+displayResult.get("resulted")+"<br>accessionId=" + accessionId + 
						"<br># of Results="+ displayRow.size()+"</td>");
						count++;
					}
			   }
				
				if(displayRow.get("PROTEIN,TOTAL")!= null) {
					Map<String, Object> displayResult = displayRow.get("PROTEIN,TOTAL");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("ALBUMIN")!= null) {
					Map<String, Object> displayResult = displayRow.get("ALBUMIN");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("TOT. BILIRUBIN")!= null) {
					Map<String, Object> displayResult = displayRow.get("TOT. BILIRUBIN");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("DIR. BILIRUBIN")!= null) {
					Map<String, Object> displayResult = displayRow.get("DIR. BILIRUBIN");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("ALKALINE PHOSPHATASE")!= null) {
					Map<String, Object> displayResult = displayRow.get("ALKALINE PHOSPHATASE");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("AST")!= null) {
					Map<String, Object> displayResult = displayRow.get("AST");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}

				if(displayRow.get("ALT")!= null) {
					Map<String, Object> displayResult = displayRow.get("ALT");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
			}
			out.println("</tr>");
		}
		out.println("</table>");
%>
<br>
<% 		
		//CBC Panel
		out.println("<table class='gridtable'>");
		out.println("<tr>");
		out.println("<th><b>CBC</b></th>");
		out.println("<th>WBC</th>");
		out.println("<th>RBC</th>");
		out.println("<th>HGB</th>");
		out.println("<th>HCT</th>");
		out.println("<th>MCV</th>");
		out.println("<th>MCHC</th>");
		out.println("<th>PLT</th>");
		out.println("<th>RDW</th>");
		out.println("<th>MPV</th>");
		out.println("</tr>");
		
		Set cbcGroup = new HashSet();
		cbcGroup.add("WBC");
		cbcGroup.add("RBC");
		cbcGroup.add("HGB");
		cbcGroup.add("HCT");
		cbcGroup.add("MCV");
		cbcGroup.add("MCHC");
		cbcGroup.add("PLT (ESTM)");
		cbcGroup.add("RDW");
		cbcGroup.add("MPV");
		
		for (String accessionId: displayMap.keySet()) {
			Map<String, Object> displayRow = displayMap.get(accessionId);
			out.println("<tr>");
			if(displayRow != null) {
				int count = 0;
				int resultCount = 0;
				
				for (String name: displayRow.keySet()) {
					if(cbcGroup.contains(name)) {
						resultCount++;
					}
			    }
			    
				//if(resultCount == 0) {
				if(resultCount < 2) {
					continue;
				}
				
				for (String name: displayRow.keySet()) {
					Map<String, Object> displayResult = displayRow.get(name);
					//Map<String, Object> resultMap = displayResult.get(name);
					if(count == 0) {
						out.println("<td>"+displayResult.get("resulted")+"<br>accessionId=" + accessionId + 
						"<br># of Results="+ displayRow.size()+"</td>");
						count++;
					}
			   }
				
				if(displayRow.get("WBC")!= null) {
					Map<String, Object> displayResult = displayRow.get("WBC");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("RBC")!= null) {
					Map<String, Object> displayResult = displayRow.get("RBC");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("HGB")!= null) {
					Map<String, Object> displayResult = displayRow.get("HGB");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("HCT")!= null) {
					Map<String, Object> displayResult = displayRow.get("HCT");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("MCV")!= null) {
					Map<String, Object> displayResult = displayRow.get("MCV");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("MCHC")!= null) {
					Map<String, Object> displayResult = displayRow.get("MCHC");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}

				if(displayRow.get("PLT (ESTM)")!= null) {
					Map<String, Object> displayResult = displayRow.get("PLT (ESTM)");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("RDW")!= null) {
					Map<String, Object> displayResult = displayRow.get("RDW");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("MPV")!= null) {
					Map<String, Object> displayResult = displayRow.get("MPV");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
			}
			out.println("</tr>");
		}
		out.println("</table>");
%>
<br>
<% 		
		//DIFF Panel
		out.println("<table class='gridtable'>");
		out.println("<tr>");
		out.println("<th><b>DIFF (CBC)</b></th>");
		out.println("<th>Neut%</th>");
		out.println("<th>Lymph%</th>");
		out.println("<th>Mono%</th>");
		out.println("<th>Eos%</th>");
		out.println("<th>Baso%</th>");
		out.println("<th>Abs Neu</th>");
		out.println("<th>Abs Lymph</th>");
		out.println("<th>Abs Mono</th>");
		out.println("<th>Abs Eos</th>");
		out.println("<th>Abs Baso</th>");
		out.println("</tr>");
		
		Set diffGroup = new HashSet();
		diffGroup.add("");
		diffGroup.add("LYMPHS");
		diffGroup.add("MONOS");
		diffGroup.add("EOSINO");
		diffGroup.add("BASO");
		
		for (String accessionId: displayMap.keySet()) {
			Map<String, Object> displayRow = displayMap.get(accessionId);
			out.println("<tr>");
			if(displayRow != null) {
				int count = 0;
				int resultCount = 0;
				
				for (String name: displayRow.keySet()) {
					if(diffGroup.contains(name)) {
						resultCount++;
					}
			    }
			    

				//if(resultCount == 0) {
				if(resultCount < 2) {
					continue;
				}
				
				for (String name: displayRow.keySet()) {
					Map<String, Object> displayResult = displayRow.get(name);
					//Map<String, Object> resultMap = displayResult.get(name);
					if(count == 0) {
						out.println("<td>"+displayResult.get("resulted")+"<br>accessionId=" + accessionId + 
						"<br># of Results="+ displayRow.size()+"</td>");
						count++;
					}
			   }
				
				if(displayRow.get("LYMPHS")!= null) {
					Map<String, Object> displayResult = displayRow.get("LYMPHS");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("MONOS")!= null) {
					Map<String, Object> displayResult = displayRow.get("MONOS");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("EOSINO")!= null) {
					Map<String, Object> displayResult = displayRow.get("EOSINO");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("BASO")!= null) {
					Map<String, Object> displayResult = displayRow.get("BASO");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
			}
			out.println("</tr>");
		}
		out.println("</table>");
%>
<br>
<% 		
		//LIPIDS Panel
		out.println("<table class='gridtable'>");
		out.println("<tr>");
		out.println("<th><b>LPIDS</b></th>");
		out.println("<th>Chol Ttl</th>");
		out.println("<th>Trig</th>");
		out.println("<th>HDL</th>");
		out.println("<th>LDL%</th>");
		out.println("<th>Chol:HDL</th>");
		out.println("</tr>");
		
		Set lipbGroup = new HashSet();
		lipbGroup.add("CHOLESTEROL");
		lipbGroup.add("TRIGLYCERIDE");
		lipbGroup.add("HDL");
		lipbGroup.add("LDL CHOLESTEROL");
		
		for (String accessionId: displayMap.keySet()) {
			Map<String, Object> displayRow = displayMap.get(accessionId);
			out.println("<tr>");
			if(displayRow != null) {
				int count = 0;
				int resultCount = 0;
				
				for (String name: displayRow.keySet()) {
					if(lipbGroup.contains(name)) {
						resultCount++;
					}
			    }
			    
				//if(resultCount == 0) {
				if(resultCount < 2) {
					continue;
				}
				
				for (String name: displayRow.keySet()) {
					Map<String, Object> displayResult = displayRow.get(name);
					//Map<String, Object> resultMap = displayResult.get(name);
					if(count == 0) {
						out.println("<td>"+displayResult.get("resulted")+"<br>accessionId=" + accessionId + 
						"<br># of Results="+ displayRow.size()+"</td>");
						count++;
					}
			   }
				
				if(displayRow.get("CHOLESTEROL")!= null) {
					Map<String, Object> displayResult = displayRow.get("CHOLESTEROL");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("TRIGLYCERIDE")!= null) {
					Map<String, Object> displayResult = displayRow.get("TRIGLYCERIDE");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("HDL")!= null) {
					Map<String, Object> displayResult = displayRow.get("HDL");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("LDL CHOLESTEROL")!= null) {
					Map<String, Object> displayResult = displayRow.get("LDL CHOLESTEROL");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
				
				if(displayRow.get("Chol:HDL")!= null) {
					Map<String, Object> displayResult = displayRow.get("Chol:HDL");
					out.println("<td>" + displayResult.get("result")+"</td>");
				} else {
					out.println("<td>&nbsp;</td>");
				}
			}
			out.println("</tr>");
		}
		out.println("</table>");
%>
</body>
</html>
