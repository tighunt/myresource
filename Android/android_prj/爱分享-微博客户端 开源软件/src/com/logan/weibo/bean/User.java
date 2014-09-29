/*
Copyright (c) 2007-2009, Yusuke Yamamoto
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of the Yusuke Yamamoto nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY Yusuke Yamamoto ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Yusuke Yamamoto BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.logan.weibo.bean;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.logan.util.WeiboResponse;
import com.weibo.net.WeiboException;

/**
 * 鐢ㄦ埛绫�
 * @author Yusuke Yamamoto
 *   
 * @version 1.0 
 *  
 */
public class User extends WeiboResponse implements java.io.Serializable {

	private static final long serialVersionUID = -332738032648843482L;
	private String id;                    //鐢ㄦ埛UID
	private String screenName="";         //寰崥鏄电О
	private String name="";               //鍙嬪ソ鏄剧ず鍚嶇О锛屽Bill Gates,鍚嶇О涓棿鐨勭┖鏍兼甯告樉绀�姝ょ壒鎬ф殏涓嶆敮鎸�
	private int province;                 //鐪佷唤缂栫爜锛堝弬鑰冪渷浠界紪鐮佽〃锛�
	private int city;                     //鍩庡競缂栫爜锛堝弬鑰冨煄甯傜紪鐮佽〃锛�
	private String location;              //鍦板潃
	private String description;           //涓汉鎻忚堪
	private String url;                   //鐢ㄦ埛鍗氬鍦板潃
	private String profileImageUrl;       //鑷畾涔夊浘鍍�
	private String userDomain;            //鐢ㄦ埛涓�鍖朥RL
	private String gender;                //鎬у埆,m--鐢凤紝f--濂�n--鏈煡
	private int followersCount=0;         //绮変笣鏁�
	private int friendsCount=0;           //鍏虫敞鏁�
	private int statusesCount=0;          //寰崥鏁�
	private int favouritesCount=0;        //鏀惰棌鏁�
	private Date createdAt;               //鍒涘缓鏃堕棿
	private boolean following;            //淇濈暀瀛楁,鏄惁宸插叧娉�姝ょ壒鎬ф殏涓嶆敮鎸�
	private boolean verified;             //鍔燰鏍囩ず锛屾槸鍚﹀井鍗氳璇佺敤鎴�
	private int verifiedType;             //璁よ瘉绫诲瀷
	private boolean allowAllActMsg;       //鏄惁鍏佽鎵�湁浜虹粰鎴戝彂绉佷俊
	private boolean allowAllComment;      //鏄惁鍏佽鎵�湁浜哄鎴戠殑寰崥杩涜璇勮
	private boolean followMe;             //姝ょ敤鎴锋槸鍚﹀叧娉ㄦ垜
	private String avatarLarge;           //澶уご鍍忓湴鍧�
	private int onlineStatus;             //鐢ㄦ埛鍦ㄧ嚎鐘舵�
	private Status status = null;         //鐢ㄦ埛鏈�柊涓�潯寰崥
	private int biFollowersCount;         //浜掔矇鏁�
	private String remark;                //澶囨敞淇℃伅锛屽湪鏌ヨ鐢ㄦ埛鍏崇郴鏃舵彁渚涙瀛楁銆�
	private String lang;                  //鐢ㄦ埛璇█鐗堟湰
	private String verifiedReason;		  //璁よ瘉鍘熷洜
	private String weihao;				  //寰櫉
	private String statusId="";
	
	
	public String getVerified_reason() {
		return verifiedReason;
	}
	public void setVerified_reason(String verifiedReason) {
		this.verifiedReason = verifiedReason;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setProvince(int province) {
		this.province = province;
	}
	public void setCity(int city) {
		this.city = city;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	public void setUserDomain(String userDomain) {
		this.userDomain = userDomain;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}
	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}
	public void setStatusesCount(int statusesCount) {
		this.statusesCount = statusesCount;
	}
	public void setFavouritesCount(int favouritesCount) {
		this.favouritesCount = favouritesCount;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public void setFollowing(boolean following) {
		this.following = following;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	public void setVerifiedType(int verifiedType) {
		this.verifiedType = verifiedType;
	}
	public void setAllowAllActMsg(boolean allowAllActMsg) {
		this.allowAllActMsg = allowAllActMsg;
	}
	public void setAllowAllComment(boolean allowAllComment) {
		this.allowAllComment = allowAllComment;
	}
	public void setFollowMe(boolean followMe) {
		this.followMe = followMe;
	}
	public void setAvatarLarge(String avatarLarge) {
		this.avatarLarge = avatarLarge;
	}
	public void setOnlineStatus(int onlineStatus) {
		this.onlineStatus = onlineStatus;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public void setBiFollowersCount(int biFollowersCount) {
		this.biFollowersCount = biFollowersCount;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public String getWeihao() {
		return weihao;
	}
	public void setWeihao(String weihao) {
		this.weihao = weihao;
	}

	public String getVerifiedReason() {
		return verifiedReason;
	}
	public void setVerifiedReason(String verifiedReason) {
		this.verifiedReason = verifiedReason;
	}
	public String getStatusId() {
		return statusId;
	}
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}
	public String getUrl() {
		return url;
	}
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	public int getVerifiedType() {
		return verifiedType;
	}
	public boolean isAllowAllActMsg() {
		return allowAllActMsg;
	}
	public boolean isAllowAllComment() {
		return allowAllComment;
	}
	public boolean isFollowMe() {
		return followMe;
	}
	public String getAvatarLarge() {
		return avatarLarge;
	}
	public int getOnlineStatus() {
		return onlineStatus;
	}
	public int getBiFollowersCount() {
		return biFollowersCount;
	}
	
	public User(JSONObject json) throws WeiboException {
		super();
		init(json);
	}

	private void init(JSONObject json) throws WeiboException {
		if(json!=null){
			try {
				id = json.getString("id");
				screenName = json.getString("screen_name");
				name = json.getString("name");
				province = json.getInt("province");
				city = json.getInt("city");
				location = json.getString("location");
				description = json.getString("description");
				url = json.getString("url");
				profileImageUrl = json.getString("profile_image_url");
				userDomain = json.getString("domain");
				gender = json.getString("gender");
				followersCount = json.getInt("followers_count");
				friendsCount = json.getInt("friends_count");
				favouritesCount = json.getInt("favourites_count");
				statusesCount = json.getInt("statuses_count");
				createdAt = parseDate(json.getString("created_at"), "EEE MMM dd HH:mm:ss z yyyy");
				//following = getBoolean("following", json);
				//verified = getBoolean("verified", json);
				verifiedType = json.getInt("verified_type"); 
				verifiedReason = json.getString("verified_reason");
				allowAllActMsg = json.getBoolean("allow_all_act_msg");
				allowAllComment = json.getBoolean("allow_all_comment");
				followMe = json.getBoolean("follow_me");
				avatarLarge = json.getString("avatar_large");
				onlineStatus = json.getInt("online_status");
				if(!json.isNull("status_id"))
				statusId = json.getString("status_id");
				biFollowersCount = json.getInt("bi_followers_count");
//				if(!json.getString("remark").isEmpty()) remark = json.getString("remark");
				lang = json.getString("lang");
				weihao = json.getString("weihao");
				if (!json.isNull("status")) status = Status.getStatus(json.getJSONObject("status"));
			} catch (JSONException jsone) {
				throw new WeiboException(jsone.getMessage() + ":" + json.toString(), jsone);
			}
		}
	}
	
//	public static String[] constructIds(Response res) throws WeiboException {
//	try {
//		JSONArray list = res.asJSONObject().getJSONArray("ids");
//		String temp = list.toString().substring(1,list.toString().length()-1);
//		String[] ids = temp.split(",");
//		return ids;
//	} catch (JSONException jsone) {
//		throw new WeiboException(jsone.getMessage() + ":" + jsone.toString(), jsone);
//	} 
//}
	/**
	 * 
	 * @param res
	 * @return
	 * @throws WeiboException
	 */
//	public static UserWapper constructWapperUsers(Response res) throws WeiboException {
//		JSONObject jsonUsers = res.asJSONObject(); //asJSONArray();
//		try {
//			JSONArray user = jsonUsers.getJSONArray("users");
//			int size = user.length();
//			List<User> users = new ArrayList<User>(size);
//			for (int i = 0; i < size; i++) {
//				users.add(new User(user.getJSONObject(i)));
//			}
//			long previousCursor = jsonUsers.getLong("previous_curosr");
//			long nextCursor = jsonUsers.getLong("next_cursor");
//			long totalNumber = jsonUsers.getLong("total_number");
//			String hasvisible = jsonUsers.getString("hasvisible");
//			return new UserWapper(users, previousCursor, nextCursor,totalNumber,hasvisible);
//		} catch (JSONException jsone) {
//			throw new WeiboException(jsone);
//		}
//	}

//	/**
//	 * @param res 
//	 * @return 
//	 * @throws WeiboException
//	 */
//	static List<User> constructResult(Response res) throws WeiboException {
//		JSONArray list = res.asJSONArray();
//		try {
//			int size = list.length();
//			List<User> users = new ArrayList<User>(size);
//			for (int i = 0; i < size; i++) {
//				users.add(new User(list.getJSONObject(i)));
//			}
//			return users;
//		} catch (JSONException e) {
//		}
//		return null;
//	}

	public String getId() {
		return id;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getName() {
		return name;
	}

	public int getProvince() {
		return province;
	}

	public int getCity() {
		return city;
	}

	public String getLocation() {
		return location;
	}

	public String getDescription() {
		return description;
	}

	public URL getProfileImageURL() {
		try {
			return new URL(profileImageUrl);
		} catch (MalformedURLException ex) {
			return null;
		}
	}

	public URL getURL() {
		try {
			return new URL(url);
		} catch (MalformedURLException ex) {
			return null;
		}
	}

	public String getUserDomain() {
		return userDomain;
	}

	public String getGender() {
		return gender;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public int getStatusesCount() {
		return statusesCount;
	}

	public int getFavouritesCount() {
		return favouritesCount;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public boolean isFollowing() {
		return following;
	}

	public boolean isVerified() {
		return verified;
	}

	public int getverifiedType() {
		return verifiedType;
	}

	public boolean isallowAllActMsg() {
		return allowAllActMsg;
	}

	public boolean isallowAllComment() {
		return allowAllComment;
	}

	public boolean isfollowMe() {
		return followMe;
	}

	public String getavatarLarge() {
		return avatarLarge;
	}

	public int getonlineStatus() {
		return onlineStatus;
	}

	public Status getStatus() {
		return status;
	}

	public int getbiFollowersCount() {
		return biFollowersCount;
	}

	public String getRemark() {
		return remark;
	}

	public String getLang() {
		return lang;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "User [" +
		"id=" + id +
		", screenName=" + screenName + 
		", name="+ name +
		", province=" + province + 
		", city=" + city +
		", location=" + location + 
		", description=" + description + 
		", url=" + url + 
		", profileImageUrl=" + profileImageUrl + 
		", userDomain=" + userDomain + 
		", gender=" + gender + 
		", followersCount=" + followersCount + 
		", friendsCount=" + friendsCount + 
		", statusesCount=" + statusesCount + 
		", favouritesCount=" + favouritesCount + 
		", createdAt=" + createdAt + 
		", following=" + following + 
		", verified=" + verified + 
		", verifiedType=" + verifiedType + 
		", allowAllActMsg=" + allowAllActMsg + 
		", allowAllComment=" + allowAllComment + 
		", followMe=" + followMe + 
		", avatarLarge=" + avatarLarge + 
		", onlineStatus=" + onlineStatus + 
		", status=" + status + 
		", biFollowersCount=" + biFollowersCount + 
		", remark=" + remark + 
		", lang=" + lang +
		", verifiedReason="  + verifiedReason +
		", weihao=" + weihao +
		", statusId=" + statusId +
		"]";
	}


}
