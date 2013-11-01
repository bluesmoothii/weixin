
<!DOCTYPE html><html> 
<head> <title>图文消息</title>
<meta http-equiv=Content-Type content="text/html;charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black"><meta name="format-detection" content="telephone=no"> 
<link rel="stylesheet" type="text/css" href="http://res.wx.qq.com/mmbizwap/zh_CN/htmledition/style/client-page1714ff.css"/>
<style>  #nickname{overflow: hidden;white-space: nowrap;text-overflow: ellipsis;max-width: 90%;  }  ol,ul{list-style-position:inside;  }</style> 
 <style>  #activity-detail .page-content .text{font-size:16px;}  </style></head> <body id="activity-detail">   
 <div class="page-bizinfo"><div class="header">
 <p class="activity-info">              
 <a href="javascript:;" id="post-user" class="activity-meta"><span class="text-ellipsis"></span>
 <i class="icon_link_arrow"></i></a></p></div></div><div class="page-content">
 <div class="media" id="media"><img src="${picUrl }" onerror="this.parentNode.removeChild(this)"/></div>
 <div class="text">${info.realName }<br/>${info.birthTime}</div>
 </div>
<script>
      (function(){
        /**
         * @description get a Max length for text, cut the long words
         * @author zemzheng
         **/
        var 
          _dom   = jQuery('.text'),
          _html0 = _dom.html();
          _em    = jQuery('<p></p>').html('a').css({display:'inline'}),
          _init  = function(){
            _em.appendTo(_dom);
            var 
              _html = _html0,
              _max  = Math.floor( _dom.width() / _em.width() ),
              _reg  = new RegExp('[a-z1-9]{' + _max + ',}', 'ig');
            _em.remove();

            _html = _html.replace(/>[^<]+</g,function(txt){
              return txt.replace(_reg, function(str){
                var _str = str, result = []
                while(_str.length > _max){
                  result.push(
                    _str.substr(0, _max)
                  );
                  _str = _str.substr(_max);
                }
                result.push(_str);
                return result.join('<br/>');
              });
            });

            _dom.html(_html);
            //console.log(_dom.html());
          };
        jQuery(window).on('resize', _init).trigger('resize');
        
      })();

      function getStrFromTxtDom(selector){
        return jQuery('#txt-' + selector)
                  .html()                  
                  .replace(/&lt;/g, '<')
                  .replace(/&gt;/g, '>');
      }
    


function viewSource(){
var UA = navigator.userAgent.toLowerCase();
var isIem = function(){
if(/IEMobile/i.test(UA)) return true;
else return false;
}
if(isIem()){
jQuery(".page-url-link:first").attr("href", getStrFromTxtDom('sourceurl') );
return ;
}
jQuery.ajax({
url: '/mp/appmsg/show-ajax' + location.search, //location.href
          async:false,
type:'POST',
timeout :2000,
data :{url:getStrFromTxtDom('sourceurl')},
complete:function(){location.href = getStrFromTxtDom('sourceurl');}
}); 
return false;
    };
          function report(link, fakeid, action_type){
            var parse_link = parseUrl(link);
            if(parse_link == null)
            {
              return ;
            }
            var query_obj = parseParams( parse_link['query_str'] );
            query_obj['action_type'] = action_type;
            query_obj['uin'] = fakeid;
            var report_url = '/mp/appmsg/show?' + jQuery.param( query_obj );
            jQuery.ajax({
              url: report_url,
              type: 'POST',
              timeout: 2000
            })
          };

          function share_scene(link, scene_type){
            var parse_link = parseUrl(link);
            if(parse_link == null)
            {
              return link;
            }
            var query_obj = parseParams( parse_link['query_str'] );
            query_obj['scene'] = scene_type;
            var share_url = 'http://' + parse_link['domain'] + parse_link['path'] + '?' + jQuery.param( query_obj ) + (parse_link['sharp'] ? parse_link['sharp'] : '');
            return share_url;
          };
          
      //report("http://mp.weixin.qq.com/mp/appmsg/show?__biz=MjM5MDA4MTk2MA==&appmsgid=10000193&itemidx=1&sign=2c19374bf9ded3cdffd91c6a2311dd58#wechat_redirect",1);
      //console.log('share url %s', share_scene("http://mp.weixin.qq.com/mp/appmsg/show?__biz=MjM5MDA4MTk2MA==&appmsgid=10000193&itemidx=1&sign=2c19374bf9ded3cdffd91c6a2311dd58#wechat_redirect", 1));
(function(){
        var onBridgeReady =  function () {
          var 
            appId  = '',
      imgUrl = "http://mmsns.qpic.cn/mmsns/9JQ3RmUl8MNmrokia37jeF45BtiauoicCtszcDQJ4I1SpPLx88SuFqVZQ/0",
      link   = "http://mp.weixin.qq.com/mp/appmsg/show?__biz=MjM5MDA4MTk2MA==&appmsgid=10000193&itemidx=1&sign=2c19374bf9ded3cdffd91c6a2311dd58#wechat_redirect",
title  = htmlDecode("面试「杯具」的经历"),
            desc   = htmlDecode("面试被拒是求职过程中的常事，各种因资格证、毕业学校、实习医院等原因被拒，你有过此类经历吗？来聊一杯你的面试「杯具」吧！"),
            fakeid = "",
            desc = desc || link;


          jQuery("#post-user").click(function(){
            WeixinJSBridge.invoke('profile',{'username':'gh_f4f286da622c','scene':'57'});
          })

// 发送给好友; 
          WeixinJSBridge.on('menu:share:appmessage', function(argv){
            
WeixinJSBridge.invoke('sendAppMessage',{
  "appid"      : appId,
  "img_url"    : imgUrl,
  "img_width"  : "640",
  "img_height" : "640",
  "link"       : share_scene(link, 1),
  "desc"       : desc,
  "title"      : title
                        }, function(res) {report(link, fakeid, 1);
                        });
});
// 分享到朋友圈;
          WeixinJSBridge.on('menu:share:timeline', function(argv){
            report(link, fakeid, 2);
WeixinJSBridge.invoke('shareTimeline',{
  "img_url"    : imgUrl,
  "img_width"  : "640",
  "img_height" : "640",
  "link"       : share_scene(link, 2),
  "desc"       : desc,
  "title"      : title
  }, function(res) {
                        });
            
});

// 分享到微博;
var weiboContent = '';
          WeixinJSBridge.on('menu:share:weibo', function(argv){
            
WeixinJSBridge.invoke('shareWeibo',{
  "content" : title + share_scene(link, 3),
  "url"     : share_scene(link, 3) 
  }, function(res) {report(link, fakeid, 3);
  });
});

// 分享到Facebook
  WeixinJSBridge.on('menu:share:facebook', function(argv){
  report(link, fakeid, 4);
  WeixinJSBridge.invoke('shareFB',{
  "img_url"    : imgUrl,
  "img_width"  : "640",
  "img_height" : "640",
  "link"       : share_scene(link, 4),
  "desc"       : desc,
  "title"      : title
  }, function(res) {} );
  });

// 隐藏右上角的选项菜单入口;
//WeixinJSBridge.call('hideOptionMenu');
};
        if(document.addEventListener){
          document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
        } else if(document.attachEvent){
          document.attachEvent('WeixinJSBridgeReady'   , onBridgeReady);
          document.attachEvent('onWeixinJSBridgeReady' , onBridgeReady);
        }
})();

(function(){

var cookie = {
get: function(name){
if( name=='' ){
return '';
        }
        var reg = new RegExp(name+'=([^;]*)');
        var res = document.cookie.match(reg);
        return (res && res[1]) || '';
    },
    set: function(name, value){
var now = new Date();
now.setDate(now.getDate() + 1);
        var exp = now.toGMTString();
        document.cookie = name + '=' + value + ';expires=' + exp;
        return true;
    }
};

var timeout = null;
var val = 0;
var url = location.search.substr(1);
var params = parseParams( url );
var biz = params['__biz'];
while( ~biz.search('=') ){
biz = biz.replace('=','#');
}
var key = biz + params['appmsgid'] + params['itemidx'];

// window.onload
jQuery(function(){
    var val = cookie.get(key);
    jQuery(window).scrollTop(val);
});

jQuery(window).bind('unload',function(){
    cookie.set(key,val);
});

jQuery(window).bind('scroll',function(){
clearTimeout(timeout);
timeout = setTimeout(function(){
val = jQuery(window).scrollTop();
},500);
});

})();

</script></body></html>