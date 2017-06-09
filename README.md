# HttpUtil
retrofit封装库

# 发起请求
## get请求
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
## 下载文件

流式下载，直接写入文件，不存到内存，避免oom

```java
 new HttpUtil.Builder("http://sw.bos.baidu.com/sw-search-sp/software/c07cde08ce4/Photoshop_CS6.exe")
                .SavePath(getExternalFilesDir(null) + File.separator + "Photoshop_CS6.exe")
                .Progress(p -> {
                    progress.setText(100 * p + "%");
                })
                .Success(s -> {
                    //返回path
                })
                .Error(t -> {
                })
                .download();
```
## rxjava

返回的流为异步

```java
new HttpUtil.Builder("url")
                .Params(map)
                .Params("key","value")
                .Version()//需要追加API版本号调用
                .Tag(this)//需要取消请求的tag
           .obpost();
               //obget() get请求
                //Obdownload()下载流;
```
 ## 取消请求

 调用时添加tag的请求
 要取消下载请求也需要给请求添加对应的独立tag

```java
   @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpUtil.cancel(this);
    }
```
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
## 请求前headers统一处理
```java
    HeadersInterceptor mHeadersInterceptor = new HeadersInterceptor() {
               @Override
               public Map checkHeaders(Map headers) {
                   //追加统一header，例：数据缓存一天
                   headers.put("Cache-Time", "3600*24");
                   return headers;
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
//               .headersInterceptor(mHeadersInterceptor)//不传不进行headers统一处理
                .build();
```

# 依赖添加
## maven
```xml
<dependency>
  <groupId>com.gengqiquan</groupId>
  <artifactId>httputil</artifactId>
  <version>1.0.2</version>
  <type>pom</type>
</dependency>
```
## gralde 
```
compile 'com.gengqiquan:httputil:1.0.2'
```
## lvy
```xml
<dependency org='com.gengqiquan' name='httputil' rev='1.0.2'>
  <artifact name='httputil' ext='pom' ></artifact>
</dependency>
```
