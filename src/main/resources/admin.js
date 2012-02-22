AJS.toInit(function() {
    var baseUrl = AJS.$("meta[name='application-base-url']").attr("content");
    
    function populateForm() {
        AJS.$.ajax({
            url: baseUrl + "/rest/plugins/1.0/",
            dataType: "json",
            success: function(config) {
                AJS.$("#cropPrefix").attr("value", config.cropPrefix);
                AJS.$("#commentUserId").attr("value", config.commentUserId);
                AJS.$("#secretKey").attr("value", config.secretKey);
                AJS.$("#gitWebServer").attr("value", config.gitWebServer);
            }
        });
    }
    
    function updateConfig() {
        AJS.$.ajax({
            url: baseUrl + "/rest/plugins/1.0/",
            type: "PUT",
            contentType: "application/json",
            data: '{ "cropPrefix": "' + AJS.$("#cropPrefix").attr("value") + '", "commentUserId": ' +  AJS.$("#commentUserId").attr("value") + '", "secretKey": ' +  AJS.$("#secretKey").attr("value") + '", "gitWebServer": ' +  AJS.$("#gitWebServer").attr("value") + ' }',
            processData: false
        });
    }
    
    populateForm();
    
    AJS.$("#admin").submit(function(e) {
        e.preventDefault();
        updateConfig();
    });
});