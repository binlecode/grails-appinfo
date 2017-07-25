<head>
    <meta name="layout" content="main" />
    <title>System logging info about ${meta(name: 'app.name')}"/></title>
    %{--<g:javascript library="jquery" plugin="jquery"/>--}%
    <asset:javascript src="application.js"/>
</head>
<body>

<g:form action='setLogLevel'>
    New Logger: <g:textField name='logger' size='75'/>
    <select onchange="setLogLevelRemote({ action: 'setLogLevel', params: generateParameters(this, true) })" name='level' id='level'>
        <g:each var='level' in='${allLevels}'>
            <option>${level}</option>
        </g:each>
    </select>
</g:form>

<br/>

<div id="accordion" style='width:100%;'>

    <h2 class="current">Loggers sorted by name</h2>
    <table cellpadding="0" cellspacing="0" border="0" class="display">
        <thead>
        <tr>
            <th>Logger</th>
            <th>Level</th>
        </tr>
        </thead>
        <tbody>
        <g:each var='logger' in='${logs}'>
            <tr>
                <td>${logger.name}</td>
                <td>
                    <select onchange="setLogLevelRemote({ params: generateParameters(this, false) })"
                            name='level_${logger.name}' id='level_${logger.name}'>
                        <g:each var='level' in='${allLevels}'>
                            <option <g:if test='${level == logger.level}'>selected='selected'</g:if>>${level}</option>
                        </g:each>
                    </select>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

</div>


<script>
    $(document).ready(function() {
//        alert('jquery ok');
    });

    function setLogLevelRemote(options) {
        alert('setLogLevelRemote - ' + options.params);
        $.ajax({
            method: 'POST',
            url: "${createLink(action: 'setLogLevel') }",
            dataType: "json",
            data: options.params,
            success: function(data) {
                alert('ok: ' + data);
            }
        });
    }

    function generateParameters(theSelect, manual) {
        var logger
        if (manual) {
            logger = $('#logger').val();
        }
        else {
            logger = theSelect.id.substring('level_'.length);
        }

        return "logger=" + escape(logger) +
                "&level=" + theSelect.options[theSelect.selectedIndex].value;
    }
</script>
</body>
