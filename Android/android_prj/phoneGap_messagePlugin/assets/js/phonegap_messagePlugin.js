var Message = function() {
};

Message.prototype = {
		
	send: function(success,error,target,content){
		//调用MessagePlugin.java 的 send(...)方法
		PhoneGap.exec(success,error,"MessagePlugin","send",[target,content]);
	}	
};

PhoneGap.addConstructor(function(){
	//在PhoneGap中添加插件，注意要在plugins.xml中给它赋权限。
	PhoneGap.addPlugin("message",new Message);
});