function renderNav(){
    $.ajax({
        type: "GET",
        url: "/api/topics",
    }).success(function(res){
        console.log("rendering nav");

        //generate the navlist element
        var navlist = $(document.createElement('div'))

        //generate the topic list
        $(res['topics']).each(function(i, topic){
            //generate subtopics sublist
            var subTopicList = $(document.createElement('ul'))
            subTopicList.attr('class', 'nav nav-pills nav-stacked')

            $(topic.subTopics).each(function(i, subTopic){
                var name = subTopic.subTopic;
                var id = subTopic.id;

                var link = $(document.createElement('a'));
                link.click(function(){
                    renderRead(id);
                })
                link.css('padding', '0px');
                link.append(name);

                var subTopicItem = $(document.createElement('li'));
                subTopicItem.attr('role', 'presentation');
                subTopicItem.append(link)

                subTopicList.append(subTopicItem)
            });


            //add topic elem
            navlist.append($(document.createElement('h3')).append(topic.topic));
            navlist.append(subTopicList);
        });

        //append navlist to DOM
        $('#navigation').html(navlist)
    });
}
