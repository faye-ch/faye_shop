// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
//项目的入口文件
import Vue from 'vue'//导入 vue.js 文件
import App from './App' //导入一级子组件
import router from './router' //导入路由文件->index.js ，若文件夹只有一个文件可以只写目录
import Vuetify from 'vuetify'//导入vue ui 框架
import config from './config'
import MyComponent from './components/MyComponent'
import './http';
import 'vuetify/dist/vuetify.min.css'
import qs from 'qs'
import 'element-ui/lib/theme-chalk/index.css';
import './assets/material.css'

Vue.use(Vuetify, { theme: config.theme})
Vue.use(MyComponent)
Vue.prototype.$qs = qs;

Vue.config.productionTip = false

/* eslint-disable no-new */
//创建 Vue 实体
new Vue({
  el: '#app', //将Vue实例绑定到 index.html 中的div id=app 容器
  router,//引入路由文件
  components: { App },//将一级子组件加入到Vue
  template: '<App/>'
})
