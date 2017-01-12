$(document).ready(
		function() {

			var realTime = setInterval(suggestWords, 2000);

			function suggestWords() {

				if ($("#shortnameV").val()) {

					$.ajax({
						type : "GET",
						url : "/suggest",
						data : {shortName : $("#shortnameV").val()},
						success : function(list) {
							
							var matches  = [];

							if (list.length > 0) {
								$("#result").html("<div><h3>Sugerencias</h3></div>");

								$.each(list, function(i, item) {
									
									matches.push(list[i]);
									
									$("#result").append(
											"sugerencia " + i
											+ ": "
											+ list[i]
											+ " ");
								});
							}
														
						},
						error : function() {
							$("#result")
							.html(
							"<div><h3>No hay sugerencias de autocompletado</h3></div>");
						}
					});
					
					$.ajax({
						type : "GET",
						url : "/suggestSynonym",
						data : {shortName : $("#shortnameV").val()},
						success : function(list) {
							
							if (list.length > 0) {
								$("#result").append("<div><h3>Sinonimos</h3></div>");

								$.each(list, function(i, item) {
								
									$("#result").append(
											"sinonimo " + i
											+ ": "
											+ list[i]
											+ " ");
								});
							}
														
						},
						error : function() {
							$("#result")
							.append(
							"<div><h3>No hay sugerencias de sinonimos</h3></div>");
						}
					});

				}else{
					$("#result")
					.html(
					"<div></div>");
				}
			}

		});