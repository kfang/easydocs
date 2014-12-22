function renderDelete(id){

    var template = $('#templates #delete-confirm').html()
    $('#content').html(template)

    $('#content #yes').click(function(){
        console.log("clicked yes")
        $.ajax({
            type: "DELETE",
            url: "/api/endpoints/"+id,
        }).success(function(){
            renderCreate();
            setTimeout(function(){
                renderNav()
            }, 500);
        }).error(function(res){
            alert(msg.responseText);
        })
    })

    $('#content #no').click(function(){
        renderRead(id);
    })

}
