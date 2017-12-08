	
<pre>
Exercise exef001: 5 min
- Where to put index jsp file.
- Where to create Controller java file.
- How extends AppController.
- How to pass parameters to JSP.
- How to put javascript in JSP.
</pre>

<h3>Parameter 1: ${p1}</h3>

<script>
	document.write("<h3>Parameter 2: ${p2}</h3>"); //variable appears as "p2" in java code
	document.write("<h3>Parameter 3: ${p3}</h3>"); //the same as writing <h3> directly in jsp
</script>
 <!--  -->
<br>
<br>
<h4>Other Internal Substitutions:</h4>    <!-- variables -->
ctx = ${ctx}<br> 
controller = ${controller}<br>
view = ${view}<br>
method = ${method}<br>
uri = ${uri}<br>  <!-- this one has result -->
url = ${url}<br>
sessionid = ${sessionid}<br>
path = ${path}<br>
referrer = ${referrer}<br>
