$(document).ready(function() {
        $('#userName').blur(function(event) {
                var name = $('#userName').val();
                $.get('JqueryServlet', {
                        userName : name
                }, function(responseText) {
                        $('#ajaxResponse').text(responseText);
                });
        });
});