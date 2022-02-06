<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="referrer" content="no-referrer"/>
    <title>${title}</title>
    <link rel="shortcut icon" href="https://www.bilibili.com/favicon.ico?v=1">
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<div class="main">
    <#list videoList as video>
        <div class="item">
            <img src="${video.picUrl}" alt="" style="width: 250px;height: 140px;" />
            <span class="name">${video.name}</span>
            <span class="author">${video.author}</span>
        </div>
    </#list>
</div>
</body>
</html>