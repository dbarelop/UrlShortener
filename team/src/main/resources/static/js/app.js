$(document).ready(
		function() {
			$("#shortener").submit(
					function(event) {
						event.preventDefault();
						
						var onSuccess = function(msg) {

							$("#result").html(
									"<div class='alert alert-success lead'>"
									+ "<a target='_blank' href='"
									+ msg.uri
									+ "'>"
									+ msg.uri
									+ "</a>"
									+ "</div>");

							$("#resultQR").html("<button id='buttonQR' class='btn btn-lg btn-primary' type='submit'>QR Code</button>");

							$('#buttonQR').click(
									function() {
										$("#QRImage").html(
												"<div><img src='" + msg.qrLink + "'></div>");
									});
						};

						if ($("#shortnameV").val()) {
							$.ajax({
								type : "POST",
								url : "/brandedLink",
								data : $(this).serialize(),
								success : onSuccess,
								error : function() {
									$("#result").html("<div class='alert alert-success lead'><p>"
											+ $("#shortnameV").val() + " already exist or not valid</p></div>");
}
							});
						}else{
							$.ajax({
								type : "POST",
								url : "/link",
								data : $(this).serialize(),
								success : onSuccess,
								error : function() {
									$("#result")
									.html(
									"<div class='alert alert-danger lead'>ERROR</div>");
								}
							});
						}

					});
		});