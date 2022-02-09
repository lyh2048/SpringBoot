<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="referrer" content="no-referrer"/>
    <title>冬奥奖牌榜</title>
    <style>
        table {
            width: 90%;
            background: #ccc;
            margin: 10px auto;
            border-collapse: collapse;
        }
        th, td {
            height: 25px;
            line-height: 25px;
            text-align: center;
            border: 1px solid #ccc;
        }
        th {
            background: #eee;
            font-weight: normal;
        }
        tr {
            background: #fff;
        }
        tr:hover {
            background: #cc0;
        }
    </style>
</head>
<body>
<table>
    <tr>
        <th>排名</th>
        <th>国旗</th>
        <th>国家/地区</th>
        <th>金牌</th>
        <th>银牌</th>
        <th>铜牌</th>
        <th>总数</th>
    </tr>
    <#list medalList as medal>
        <tr>
            <td>${medal_index + 1}</td>
            <td>
                <img src="${medal.img}" alt="" />
            </td>
            <td>${medal.name}</td>
            <td>${medal.gold}</td>
            <td>${medal.silver}</td>
            <td>${medal.bronze}</td>
            <td>${medal.total}</td>
        </tr>
    </#list>
</table>
</body>
</html>