# 1.技术说明

前端技术：

- 基础的HTML、CSS、JavaScript（基于ES6标准）
- JQuery
- Vue.js 2.0以及基于Vue的框架：Vuetify（UI框架）
- 前端构建工具：WebPack
- 前端安装包工具：NPM
- Vue脚手架：Vue-cli
- Vue路由：vue-router
- ajax框架：axios
- 基于Vue的富文本框架：quill-editor 

后端技术：

- 基础的SpringMVC、Spring 5.x和MyBatis3
- Spring Boot 2.0.7版本
- Spring Cloud 最新版 Finchley.SR2
- Redis-4.0 ：临时存储服务端生成的手机短信验证码
- RabbitMQ-3.4 ：微服务间的通信中间件
- Elasticsearch-6.3 ：搜索引擎
- nginx-1.14.2 ：部署前端静态资源，反向代理解决跨域问题，域名映射
- FastDFS - 5.0.8：存储商品图片
- Thymeleaf 
- mysql 5.6



# 2.部署说明

一、主要项目：

都在idea中打开

1.leyou-portal(前台) : 在 Terminal 中输入指令：live-server --port=9002

2.leyou-manage-web(后台): 在 Terminal 中输入指令运行指令: npm start，若运行出错则删掉 node_modules 目录，重新执行npm install 命令，再运行。

3.faye (服务器): 启动微服务

 

二、工具及中间件

  1.SwitchHosts : 修改主机地址 HOST，可以映射到自定义的域名

  2.nginx : 运行指令: start nginx 重新加载:nginx -s reload，端口的反向代理。

  3.在Linux中安装nginx

  4.FastDFS : 分布式文件系统，安装在虚拟机中，打开虚拟机自动启动。如果图片不显示，则在Linux 命令中输入：nginx ，启动nginx服务器

 

  5.elasticsearch : 搜索引擎，需要配套安装 Kibana(界面管理工具) ik(中文分词          器)，点击 elasticsearch.bat 即可运行，Kibnana运行同理

  6.rabbitmq : 消息队列，用作数据同步。运行 rabbitmq-server.exe文件

​      需要安装 Erlang 语言

​      进入管理界面：127.0.0.1:15672

  7.redis : 键值对数据库 ，点击 redis-server-exe 运行

  8.redisDesktopManager: redis 管理工具

  9.Postman : 请求模拟工具

  

三、数据库

  1.Sql文件：dp.sql

  2.先创建数据库：hm49

  3.数据库默认密码：root

  

四、短信验证码服务，需要自行注册阿里云短信服务



# 3.效果展示

后台

分类管理

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/caab184337674098bd330a2655734748~tplv-k3u1fbpfcp-watermark.image?imageslim)

规格参数管理

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7451c056dfae43e7af70532ce12dec2d~tplv-k3u1fbpfcp-watermark.image?imageslim)

品牌管理

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e0587d691b234aef950898bbf91b5ea9~tplv-k3u1fbpfcp-watermark.image?imageslim)

前台首页

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/bdf45dfe61ae483aa4a727233132131b~tplv-k3u1fbpfcp-watermark.image)

<img src="https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3bffb3f8e92042be96df18cc6b6fc6a4~tplv-k3u1fbpfcp-watermark.image" style="zoom:150%;" />



![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1fe470e6f5a84b0d99f1a53594dfd774~tplv-k3u1fbpfcp-watermark.image)

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e1265c8d5c4c4c34b9bc61c30b6c8932~tplv-k3u1fbpfcp-watermark.image)

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1ddbe132f617488086fe62d1b3bdf71a~tplv-k3u1fbpfcp-watermark.image?imageslim)



![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/aa272c7c60d041afbf623d0d2e41db8e~tplv-k3u1fbpfcp-watermark.image?imageslim)

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2cd2988bf9c94a9fb4d6b9f1cd7327d2~tplv-k3u1fbpfcp-watermark.image?imageslim)
