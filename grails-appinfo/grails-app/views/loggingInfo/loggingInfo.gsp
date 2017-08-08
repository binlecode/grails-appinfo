<head>
    <meta name="layout" content="main" />
    <title>System logging info about ${meta(name: 'app.name')}"/></title>
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

    <g:each in="${logs.keySet().sort()}" var="logGroupKey">
        <h2 class="current">${logGroupKey}</h2>
        <div id="${logGroupKey}_div">
            <table id="${logGroupKey}_table" cellpadding="0" cellspacing="0" border="0" class="display">
                <thead>
                <tr>
                    <th>Logger</th>
                    <th>Level</th>
                </tr>
                </thead>
                <tbody>
                <g:each var='loggerNameAndLevel' in='${logs.get(logGroupKey)}'>
                    <tr>
                        <td>${loggerNameAndLevel.name}</td>
                        <td>
                            <select onchange="setLogLevelRemote({ params: generateParameters(this, false) })"
                                    name='level_${loggerNameAndLevel.name}' id='level_${loggerNameAndLevel.name}'>
                                <g:each var='level' in='${allLevels}'>
                                    <option <g:if test='${level == loggerNameAndLevel.level}'>selected='selected'</g:if>>${level}</option>
                                </g:each>
                            </select>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>
    </g:each>



    <h2>Services</h2>

    <div id="serviceHolder" class="pane">
        <table id="service" cellpadding="0" cellspacing="0" border="0" class="display">
            <thead>
            <tr>
                <th>Logger</th>
                <th>Level</th>
            </tr>
            </thead>
            <tbody>
            <g:each var='loggerNameAndLevel' in='${service}'>
                <tr>
                    <td>${loggerNameAndLevel.name}</td>
                    <td>
                        <select onchange="remoteFunction({ action: 'setLogLevel', params: generateParameters(this, false) })"
                                name='level_${loggerNameAndLevel.name}' id='level_${loggerNameAndLevel.name}'>
                            <g:each var='level' in='${allLevels}'>
                                <option <g:if
                                                test='${level == loggerNameAndLevel.level}'>selected='selected'</g:if>>${level}</option>
                            </g:each>
                        </select>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>

    <h2>TagLibs</h2>

    <div id="taglibHolder" class="pane">
        <table id="taglib" cellpadding="0" cellspacing="0" border="0" class="display">
            <thead>
            <tr>
                <th>Logger</th>
                <th>Level</th>
            </tr>
            </thead>
            <tbody>
            <g:each var='loggerNameAndLevel' in='${taglib}'>
                <tr>
                    <td>${loggerNameAndLevel.name}</td>
                    <td>
                        <select onchange="remoteFunction({ action: 'setLogLevel', params: generateParameters(this, false) })"
                                name='level_${loggerNameAndLevel.name}' id='level_${loggerNameAndLevel.name}'>
                            <g:each var='level' in='${allLevels}'>
                                <option <g:if test='${level == loggerNameAndLevel.level}'>selected='selected'</g:if>>${level}</option>
                            </g:each>
                        </select>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>

    <h2>Grails</h2>

    <div id="grailsHolder" class="pane">
        <table id="grails" cellpadding="0" cellspacing="0" border="0" class="display">
            <thead>
            <tr>
                <th>Logger</th>
                <th>Level</th>
            </tr>
            </thead>
            <tbody>
            <g:each var='loggerNameAndLevel' in='${grails}'>
                <tr>
                    <td>${loggerNameAndLevel.name}</td>
                    <td>
                        <select onchange="remoteFunction({ action: 'setLogLevel', params: generateParameters(this, false) })"
                                name='level_${loggerNameAndLevel.name}' id='level_${loggerNameAndLevel.name}'>
                            <g:each var='level' in='${allLevels}'>
                                <option <g:if test='${level == loggerNameAndLevel.level}'>selected='selected'</g:if>>${level}</option>
                            </g:each>
                        </select>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>

    <h2>Groovy</h2>

    <div id="groovyHolder" class="pane">
        <table id="groovy" cellpadding="0" cellspacing="0" border="0" class="display">
            <thead>
            <tr>
                <th>Logger</th>
                <th>Level</th>
            </tr>
            </thead>
            <tbody>
            <g:each var='loggerNameAndLevel' in='${groovy}'>
                <tr>
                    <td>${loggerNameAndLevel.name}</td>
                    <td>
                        <select onchange="remoteFunction({ action: 'setLogLevel', params: generateParameters(this, false) })"
                                name='level_${loggerNameAndLevel.name}' id='level_${loggerNameAndLevel.name}'>
                            <g:each var='level' in='${allLevels}'>
                                <option <g:if test='${level == loggerNameAndLevel.level}'>selected='selected'</g:if>>${level}</option>
                            </g:each>
                        </select>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>

    <h2>Spring</h2>

    <div id="springHolder" class="pane">
        <table id="spring" cellpadding="0" cellspacing="0" border="0" class="display">
            <thead>
            <tr>
                <th>Logger</th>
                <th>Level</th>
            </tr>
            </thead>
            <tbody>
            <g:each var='loggerNameAndLevel' in='${spring}'>
                <tr>
                    <td>${loggerNameAndLevel.name}</td>
                    <td>
                        <select onchange="remoteFunction({ action: 'setLogLevel', params: generateParameters(this, false) })"
                                name='level_${loggerNameAndLevel.name}' id='level_${loggerNameAndLevel.name}'>
                            <g:each var='level' in='${allLevels}'>
                                <option <g:if test='${level == loggerNameAndLevel.level}'>selected='selected'</g:if>>${level}</option>
                            </g:each>
                        </select>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>

    <h2>Hibernate</h2>

    <div id="hibernateHolder" class="pane">
        <table id="hibernate" cellpadding="0" cellspacing="0" border="0" class="display">
            <thead>
            <tr>
                <th>Logger</th>
                <th>Level</th>
            </tr>
            </thead>
            <tbody>
            <g:each var='loggerNameAndLevel' in='${hibernate}'>
                <tr>
                    <td>${loggerNameAndLevel.name}</td>
                    <td>
                        <select onchange="remoteFunction({ action: 'setLogLevel', params: generateParameters(this, false) })"
                                name='level_${loggerNameAndLevel.name}' id='level_${loggerNameAndLevel.name}'>
                            <g:each var='level' in='${allLevels}'>
                                <option <g:if test='${level == loggerNameAndLevel.level}'>selected='selected'</g:if>>${level}</option>
                            </g:each>
                        </select>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>

    <h2>Misc</h2>

    <div id="miscHolder" class="pane">
        <table id="misc" cellpadding="0" cellspacing="0" border="0" class="display">
            <thead>
            <tr>
                <th>Logger</th>
                <th>Level</th>
            </tr>
            </thead>
            <tbody>
            <g:each var='loggerNameAndLevel' in='${misc}'>
                <tr>
                    <td>${loggerNameAndLevel.name}</td>
                    <td>
                        <select onchange="remoteFunction({
                            action: 'setLogLevel',
                            params: generateParameters(this, false)
                        })"
                                name='level_${loggerNameAndLevel.name}' id='level_${loggerNameAndLevel.name}'>
                            <g:each var='level' in='${allLevels}'>
                                <option
                                    <g:if test='${level == loggerNameAndLevel.level}'>selected='selected'</g:if>>${level}</option>
                            </g:each>
                        </select>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>

</div>


<script>
    $(document).ready(function() {
        alert('jquery ok');
        <%
            for (name in ['spring', 'hibernate', 'codec', 'controller', 'controllerMixin',
                          'domain', 'filters', 'service', 'taglib', 'grails', 'groovy', 'misc']) {
                out << "\t\$('#$name').dataTable( { 'bAutoWidth': false } );\n"
            }
        %>
        $("#accordion").tabs("#accordion div.pane", {tabs: 'h2', effect: 'default', initialIndex: null});
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
