# HttpUtil
retrofit封装库

# 初始化配置
## 请求前参数统一处理，追加渠道名称用户手机号之类
```java
   ParamsInterceptor mParamsInterceptor = new ParamsInterceptor() {
            @Override
            public Map checkParams(Map params) {
                //追加统一参数
                params.put("app_type", "android_price");
                return params;
            }
        };
```
## 应用入口进行初始化
```java
   new HttpUtil.SingletonBuilder(getApplicationContext())
                .baseUrl("baseurl")//URL请求前缀地址。必传
//                .versionApi("")//API版本，不传不可以追加接口版本号
//                .addServerUrl("")//备份服务器ip地址，可多次调用传递
//                .addCallFactory()//不传默认StringConverterFactory
//                .addConverterFactory()//不传默认RxJavaCallAdapterFactory
//                .client()//OkHttpClient,不传默认OkHttp3
                .paramsInterceptor(mParamsInterceptor)//不传不进行参数统一处理
                .build();
```

# 发起请求 
## get请求
```java
new HttpUtil.Builder("url")
                .Params(map)
                .Params("key","value")
                .Version()//需要追加API版本号调用
                .Tag(this)//需要取消请求的tag
                .Success(new Success() {
                    @Override
                    public void Success(String model) {

                    }
                })
                .Error(new Error() {
                    @Override
                    public void Error(Object... values) {

                    }
                })
                .get();
```
## post请求
```java
new HttpUtil.Builder("url")
                .Params(map)
                .Params("key","value")
                .Version()//需要追加API版本号调用
                .Tag(this)//需要取消请求的tag
                .Success(new Success() {
                    @Override
                    public void Success(String model) {

                    }
                })
                .Error(new Error() {
                    @Override
                    public void Error(Object... values) {

                    }
                })
                .post();
```
## lambda写法

```  java
 new HttpUtil.Builder("url")
                .Params(map)
                .Params("key","value")
                .Version()//需要追加API版本号调用
                .Tag(this)//需要取消请求的tag
                .Success(str->{
                    //do something surccess
                })
                .Error(v->{
                    //deal something error 
                })
                .get();
```
## rxjava 

返回的流为异步

```java
new HttpUtil.Builder("url")
                .Params(map)
                .Params("key","value")
                .Version()//需要追加API版本号调用
                .Tag(this)//需要取消请求的tag
                .obpost();//get请求则为：obget()
```
 ## 取消请求
 调用时添加tag的请求
```java
   @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpUtil.cancel(this);
    }
```
# 依赖添加
## maven
```xml
<dependency>
  <groupId>com.gengqiquan.httputil</groupId>
  <artifactId>library</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```
## gralde 
```
compile 'com.gengqiquan.httputil:library:1.0.0'
```
## lvy
```xml
<dependency org='com.gengqiquan.httputil' name='library' rev='1.0.0'>
  <artifact name='library' ext='pom' ></artifact>
</dependency>
```
