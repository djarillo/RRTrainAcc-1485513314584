<script type="text/javascript">
$(document).ready(function (){

    $('.menu-wrap .menu ul li a').each(function(){
        var path = window.location.href;
        var current = path.substring(path.lastIndexOf('/'));
        var url = $(this).attr('href');

        if(url == current){
        	$('.menu-wrap .menu ul li.current-item').removeClass('current-item');
            $(this).parent().addClass('current-item');
        };
    });         
});
</script>
<div class="menu-wrap">
    <nav class="menu">
        <ul class="clearfix">
            <li class="current-item"><a href="/">Home</a></li>
            <li><a href="/Refinement">Refinement</a></li>
            <li><a href="/statistics">Statistics</a></li>
            <li><a href="/Admin">Upload File</a></li>
            <li><a href="/Accuracy">Accuracy</a></li>
        </ul>
    </nav>
</div>