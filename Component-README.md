### 组件化开发

#### 说明

本文仅作案例演示，方便学习和掌握基础知识。下面先明确一下能实现的功能和用到的技术点。

技术点： 

- 多module工程，有baselib和主app以及多业务module
- 多module，实现某个module可独立运行
- 多module之间跳转，使用ARouter框架



项目功能架构：

![img](https://github.com/YBill/ARouterTest/blob/master/sc/component/app.png)

#### 路由应用场景

安卓的项目结构发展越来越倾向于多模块，而模块间的跳转如果使用原生方式（`Intent跳转`），那么会随着项目的发展壮大，最终导致错综复杂的相互关联，从而给维护带来很大的麻烦，如下图这样：

![img](https://github.com/YBill/ARouterTest/blob/master/sc/component/component_2.jpg)

> 黑色线条：表示依赖，有了依赖，就可以在当前模块引用其他模块的类，就可以使用`Intent` 跳转。

> 绿色线条：表示从app模块要跳转login,live,work模块的某个页面，那么必须依赖对应模块才可以引用到相关类，从而实现跳转。

> 红色线条：业务需要，从detail模块可以直接进入comment列表，那么detail模块就必须依赖comment模块；反之亦然。

> 蓝色线条：当用户没有登录，或者登录状态失效，亦或者账号在别处登录了，那么需要从当前模块跳转到login模块，所以comment模块要依赖login模块。



这样随着项目功能的拓展，带来的问题就很明显了。

而`ARouter`的出现，就很好的解决了这个问题，其功能很强大，对于多模块的项目，无论是否组件化，都很好的解决了相互依赖和跳转带来的维护成本。使用ARouter解耦后如下图：

![img](https://github.com/YBill/ARouterTest/blob/master/sc/component/component_1.jpg)

> 黑色线条：表示依赖关系，这里的依赖主要是解决资源共用问题，而不是跳转。如果用不到baselib中的资源，那么无需依赖。

> 其他线条：表示无需相互依赖，就可以实现页面跳转和通信，这就是路由的强大之处。

#### 工程 Module 配置

`baselib`是存放公共文件；`list`、`detail`、`comment`、`mine`分别是列表、详情、评论、我的四个单独模块，分别都依赖 baselib，并且四个模块都可以独立运行；`app` 是项目主入口，依赖 list、detail、comment、mine。项目结构如下图：

![img](https://github.com/YBill/ARouterTest/blob/master/sc/component/project.png)

配置如下：

```groovy
ext {
    // false 集成模式  true 组件模式可单独运行
    isListComponent = false
    isDetailComponent = false
    isCommentComponent = false
    isMineComponent = false
}
```
