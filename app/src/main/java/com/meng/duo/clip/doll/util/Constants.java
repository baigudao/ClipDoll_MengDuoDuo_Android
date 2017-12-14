package com.meng.duo.clip.doll.util;

/**
 * Created by Devin on 2017/12/11 10:22
 * E-mail:971060378@qq.com
 */

public class Constants {

    //一般常量
    public static final String APP_ID = "wx559f2fa5ea3e63ba";//微信开放平台AppID
    public static final String FRAGMENT_NAME = "fragment_name";
    public static final int LIVE_APPID = 1400054324;//   1400051630      1400048722  1400054324
    public static final int ACCOUNT_TYPE = 20189;//      19613           19064       20189
    public static final String IS_ENTER_GUIDE_VIEW = "is_enter_guide_view";
    public static final String IS_USER_LOGIN = "is_user_login";
    public static final String LOGINTYPE = "loginType";
    public static final String PLATFORM = "platform";
    public static final String WECHATCODE = "wechatCode";
    public static final String SESSION = "session";
    public static final String HEADIMG = "headImg";
    public static final String INVITECODE = "inviteCode";
    public static final String NICKNAME = "nickName";
    public static final String USERID = "userId";
    public static final String DEVICE_ID = "device_id";
    public static final String MY_USER_INFO = "my_user_info";
    public static final String TLSSIGN = "tlsSign";
    public static final String OPINION = "opinion";
    public static final String GROUPID = "groupId";
    public static final String ROOMID = "roomId";
    public static final String PAGENUM = "pageNum";
    public static final String PAGESIZE = "pageSize";
    public static final String RESULT = "result";
    public static final String CONTENT = "content";
    public static final String TYPE = "type";
    public static final String FIRSTLOGIN = "firstLogin";
    public static final String FROMINVITECODE = "fromInviteCode";
    public static final String IS_PLAY_BACKGROUND_SOUND = "is_play_background_sound";
    public static final String IS_PLAY_BACKGROUND_MUSIC = "is_play_background_music";
    public static final int PAGE_SIZE = 10;

    // 直播角色
    public static final String ROLE_MASTER = "LiveMaster";
    public static final String ROLE_GUEST = "Guest";
    public static final String ROLE_LIVEGUEST = "LiveGuest";


    //网络常量
    private static final String BASE_URL = "https://wwapi.91tmedia.com/wawa_api/";//http://119.29.238.35:8081   https://wwapi.91tmedia.com
    private static final String homeRoomListUrl = BASE_URL + "live/room/getHomeRoomList/v1";
    private static final String homeBannerUrl = BASE_URL + "home/getBannerList/v1";
    private static final String clipDollRecordUrl = BASE_URL + "playRecord/getPlayRecordList/v1";
    private static final String userLoginUrl = BASE_URL + "user/account/login/v1";
    private static final String liveSigUrl = BASE_URL + "user/getTlsSign/v1";
    private static final String userFeedBack = BASE_URL + "feedback/saveFeedback/v1";
    private static final String userInfo = BASE_URL + "user/getUserInfo/v1";
    private static final String userBalance = BASE_URL + "user/getUserBalance/v1";//得到余额
    private static final String applyBeginGame = BASE_URL + "live/room/applyBeginGame/v1";
    private static final String gameOverUrl = BASE_URL + "live/room/gameover/v1";
    private static final String coinCostRecordUrl = BASE_URL + "user/getCoinRecords/v1";//游戏币消费记录
    private static final String myNotifyUrl = BASE_URL + "message/getMyNotify/v1";//我的通知

    private static final String verifyInviteUrl = BASE_URL + "inviteAward/verifyInviteCode/v1";//邀请有奖
    private static final String inviteCodeUrl = BASE_URL + "inviteAward/getInviteCodeByUserId/v1";//获取用户兑换过的邀请码

    private static final String logoutUrl = BASE_URL + "user/account/logout/v1";//登录注销

    private static final String rechargePriceList = BASE_URL + "recharge/getLqbPriceByPlatformType/v1";//获取价目表
    private static final String rechargeUrl = BASE_URL + "recharge/rechargeLqb/v1";//虚拟充值
    private static final String isFirstRechargeUrl = BASE_URL + "recharge/isFirstRecharge/v1";//是否首充
    private static final String weChatPayUrl = BASE_URL + "recharge/wechatPay/v1";//微信充值

    private static final String waitingSendUrl = BASE_URL + "order/unsentProducts/v1";//待发货
    private static final String sendOverUrl = BASE_URL + "order/sentedProducts/v1";//待发货

    private static final String userAddressSaveUrl = BASE_URL + "user/address/save/v1";//保存地址
    private static final String userAddressListUrl = BASE_URL + "user/address/list/v1";//地址列表
    private static final String userAddressDeleteUrl = BASE_URL + "user/address/delete/v1";//删除地址

    private static final String checkVersionUrl = BASE_URL + "version/checkAppVersionByVersion/v1";//获取版本信息
    private static final String userProtocolUrl = "http://wwh5.91tmedia.com/privacy";//用户协议

    private static final String applyBeginGameUrl = BASE_URL + "live/room/applyBeginGameFromMDD/v1";//申请开始游戏(非腾讯)
    private static final String playGameUrl = BASE_URL + "live/room/playGeme/v1";//游戏操控(非腾讯)
    private static final String gameResultUrl = BASE_URL + "live/room/getMyGameResult/v1";//游戏结果(非腾讯)
    private static final String liveRoomStateUrl = BASE_URL + "live/room/getLiveRoomState/v1";//房间状态
    private static final String roomStateUrl = BASE_URL + "live/room/getRoomStateById/v1";//获取直播间状态
    private static final String addUserForLiveRoomUrl = BASE_URL + "live/room/addUserForLiveRoom/v1";//用户加入直播间 计数
    private static final String removeUserForLiveRoomUrl = BASE_URL + "live/room/removeUserForLiveRoom/v1";//用户退出直播间 计数
    private static final String liveRoomUserUrl = BASE_URL + "live/room/getLiveRoomUser/v1";//获取直播间观众数和玩家 计数

    private static final String feedBackPostServerUrl = BASE_URL + "terminal/fault/save/v1";// 上报故障

    public static String getHomeRoomListUrl() {
        return homeRoomListUrl;
    }

    public static String getHomeBannerUrl() {
        return homeBannerUrl;
    }

    public static String getClipDollRecordUrl() {
        return clipDollRecordUrl;
    }

    public static String getUserLoginUrl() {
        return userLoginUrl;
    }

    public static String getLiveSigUrl() {
        return liveSigUrl;
    }

    public static String getUserFeedBack() {
        return userFeedBack;
    }

    public static String getUserInfo() {
        return userInfo;
    }

    public static String getUserBalance() {
        return userBalance;
    }

    public static String getApplyBeginGame() {
        return applyBeginGame;
    }

    public static String getGameOverUrl() {
        return gameOverUrl;
    }

    public static String getCoinCostRecordUrl() {
        return coinCostRecordUrl;
    }

    public static String getMyNotifyUrl() {
        return myNotifyUrl;
    }

    public static String getVerifyInviteUrl() {
        return verifyInviteUrl;
    }

    public static String getLogoutUrl() {
        return logoutUrl;
    }

    public static String getRechargePriceList() {
        return rechargePriceList;
    }

    public static String getRechargeUrl() {
        return rechargeUrl;
    }

    public static String getWaitingSendUrl() {
        return waitingSendUrl;
    }

    public static String getSendOverUrl() {
        return sendOverUrl;
    }

    public static String getUserAddressSaveUrl() {
        return userAddressSaveUrl;
    }

    public static String getUserAddressListUrl() {
        return userAddressListUrl;
    }

    public static String getUserAddressDeleteUrl() {
        return userAddressDeleteUrl;
    }

    public static String getCheckVersionUrl() {
        return checkVersionUrl;
    }

    public static String getIsFirstRechargeUrl() {
        return isFirstRechargeUrl;
    }

    public static String getWeChatPayUrl() {
        return weChatPayUrl;
    }

    public static String getApplyBeginGameUrl() {
        return applyBeginGameUrl;
    }

    public static String getUserProtocolUrl() {
        return userProtocolUrl;
    }

    public static String getPlayGameUrl() {
        return playGameUrl;
    }

    public static String getGameResultUrl() {
        return gameResultUrl;
    }

    public static String getLiveRoomStateUrl() {
        return liveRoomStateUrl;
    }

    public static String getInviteCodeUrl() {
        return inviteCodeUrl;
    }

    public static String getRoomStateUrl() {
        return roomStateUrl;
    }

    public static String getAddUserForLiveRoomUrl() {
        return addUserForLiveRoomUrl;
    }

    public static String getRemoveUserForLiveRoomUrl() {
        return removeUserForLiveRoomUrl;
    }

    public static String getLiveRoomUserUrl() {
        return liveRoomUserUrl;
    }

    public static String getFeedBackPostServerUrl() {
        return feedBackPostServerUrl;
    }
}
