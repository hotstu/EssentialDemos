//package github.hotstu.demo.smack;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import org.jivesoftware.smack.*;
//
//import org.jivesoftware.smack.filter.AndFilter;
//import org.jivesoftware.smack.filter.PacketFilter;
//import org.jivesoftware.smack.filter.PacketIDFilter;
//import org.jivesoftware.smack.filter.PacketTypeFilter;
//import org.jivesoftware.smack.packet.IQ;
//import org.jivesoftware.smack.packet.Message;
//import org.jivesoftware.smack.packet.Packet;
//import org.jivesoftware.smack.packet.Presence;
//import org.jivesoftware.smack.provider.ProviderManager;
//import org.jivesoftware.smack.util.StringUtils;
//import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
//import org.jivesoftware.smackx.filetransfer.FileTransferManager;
//import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
//import org.jivesoftware.smackx.muc.DiscussionHistory;
//import org.jivesoftware.smackx.muc.HostedRoom;
//import org.jivesoftware.smackx.muc.MultiUserChat;
//import org.jivesoftware.smackx.search.UserSearch;
//import org.jivesoftware.smackx.search.UserSearchManager;
//
//import android.graphics.drawable.Drawable;
//import android.util.Log;
//
///**
// * XmppConnection 工具类
// *
// * @author 肖赛SoAi
// */
//public class XmppConnection {
//    private int SERVER_PORT = 5222;
//    private String SERVER_HOST = "127.0.0.1";
//    private AbstractXMPPConnection connection = null;
//    private String SERVER_NAME = "ubuntuserver4java";
//
//
//
//
//    /**
//     * 更改用户状态
//     */
//    public void setPresence(int code) {
//        XMPPConnection con = getConnection();
//        if (con == null)
//            return;
//        Presence presence;
//        switch (code) {
//            case 0:
//                presence = new Presence(Presence.Type.available);
//                con.sendPacket(presence);
//                Log.v("state", "设置在线");
//                break;
//            case 1:
//                presence = new Presence(Presence.Type.available);
//                presence.setMode(Presence.Mode.chat);
//                con.sendPacket(presence);
//                Log.v("state", "设置Q我吧");
//                break;
//            case 2:
//                presence = new Presence(Presence.Type.available);
//                presence.setMode(Presence.Mode.dnd);
//                con.sendPacket(presence);
//                Log.v("state", "设置忙碌");
//                break;
//            case 3:
//                presence = new Presence(Presence.Type.available);
//                presence.setMode(Presence.Mode.away);
//                con.sendPacket(presence);
//                Log.v("state", "设置离开");
//                break;
//            case 4:
//                Roster roster = con.getRoster();
//                Collection<RosterEntry> entries = roster.getEntries();
//                for (RosterEntry entry : entries) {
//                    presence = new Presence(Presence.Type.unavailable);
//                    presence.setPacketID(Packet.ID_NOT_AVAILABLE);
//                    presence.setFrom(con.getUser());
//                    presence.setTo(entry.getUser());
//                    con.sendPacket(presence);
//                    Log.v("state", presence.toXML());
//                }
//                // 向同一用户的其他客户端发送隐身状态
//                presence = new Presence(Presence.Type.unavailable);
//                presence.setPacketID(Packet.ID_NOT_AVAILABLE);
//                presence.setFrom(con.getUser());
//                presence.setTo(StringUtils.parseBareAddress(con.getUser()));
//                con.sendPacket(presence);
//                Log.v("state", "设置隐身");
//                break;
//            case 5:
//                presence = new Presence(Presence.Type.unavailable);
//                con.sendPacket(presence);
//                Log.v("state", "设置离线");
//                break;
//            default:
//                break;
//        }
//    }
//
//    /**
//     * 获取所有组
//     *
//     * @return 所有组集合
//     */
//    public List<RosterGroup> getGroups() {
//        if (getConnection() == null)
//            return null;
//        List<RosterGroup> grouplist = new ArrayList<RosterGroup>();
//        Collection<RosterGroup> rosterGroup = getConnection().getRoster()
//                .getGroups();
//        Iterator<RosterGroup> i = rosterGroup.iterator();
//        while (i.hasNext()) {
//            grouplist.add(i.next());
//        }
//        return grouplist;
//    }
//
//    /**
//     * 获取某个组里面的所有好友
//     *
//     * @param roster
//     * @param groupName 组名
//     * @return
//     */
//    public List<RosterEntry> getEntriesByGroup(String groupName) {
//        if (getConnection() == null)
//            return null;
//        List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();
//        RosterGroup rosterGroup = getConnection().getRoster().getGroup(
//                groupName);
//        Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
//        Iterator<RosterEntry> i = rosterEntry.iterator();
//        while (i.hasNext()) {
//            Entrieslist.add(i.next());
//        }
//        return Entrieslist;
//    }
//
//    /**
//     * 获取所有好友信息
//     *
//     * @return
//     */
//    public List<RosterEntry> getAllEntries() {
//        if (getConnection() == null)
//            return null;
//        List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();
//        Collection<RosterEntry> rosterEntry = getConnection().getRoster()
//                .getEntries();
//        Iterator<RosterEntry> i = rosterEntry.iterator();
//        while (i.hasNext()) {
//            Entrieslist.add(i.next());
//        }
//        return Entrieslist;
//    }
//
//    /**
//     * 获取用户VCard信息
//     *
//     * @param connection
//     * @param user
//     * @return
//     * @throws XMPPException
//     */
//    public VCard getUserVCard(String user) {
//        if (getConnection() == null)
//            return null;
//        VCard vcard = new VCard();
//        try {
//            vcard.load(getConnection(), user);
//        } catch (XMPPException e) {
//            e.printStackTrace();
//        }
//        return vcard;
//    }
//
//    /**
//     * 获取用户头像信息
//     *
//     * @param connection
//     * @param user
//     * @return
//     */
//    public Drawable getUserImage(String user) {
//        if (getConnection() == null)
//            return null;
//        ByteArrayInputStream bais = null;
//        try {
//            VCard vcard = new VCard();
//            // 加入这句代码，解决No VCard for
//            ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
//                    new org.jivesoftware.smackx.provider.VCardProvider());
//            if (user == "" || user == null || user.trim().length() <= 0) {
//                return null;
//            }
//            vcard.load(getConnection(), user + "@"
//                    + getConnection().getServiceName());
//
//            if (vcard == null || vcard.getAvatar() == null)
//                return null;
//            bais = new ByteArrayInputStream(vcard.getAvatar());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        return FormatTools.getInstance().InputStream2Drawable(bais);
//    }
//
//    /**
//     * 添加一个分组
//     *
//     * @param groupName
//     * @return
//     */
//    public boolean addGroup(String groupName) {
//        if (getConnection() == null)
//            return false;
//        try {
//            getConnection().getRoster().createGroup(groupName);
//            Log.v("addGroup", groupName + "創建成功");
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * 删除分组
//     *
//     * @param groupName
//     * @return
//     */
//    public boolean removeGroup(String groupName) {
//        return true;
//    }
//
//    /**
//     * 添加好友 无分组
//     *
//     * @param userName
//     * @param name
//     * @return
//     */
//    public boolean addUser(String userName, String name) {
//        if (getConnection() == null)
//            return false;
//        try {
//            getConnection().getRoster().createEntry(userName, name, null);
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * 添加好友 有分组
//     *
//     * @param userName
//     * @param name
//     * @param groupName
//     * @return
//     */
//    public boolean addUser(String userName, String name, String groupName) {
//        if (getConnection() == null)
//            return false;
//        try {
//            Presence subscription = new Presence(Presence.Type.subscribed);
//            subscription.setTo(userName);
//            userName += "@" + getConnection().getServiceName();
//            getConnection().sendPacket(subscription);
//            getConnection().getRoster().createEntry(userName, name,
//                    new String &#91;&#93;
//            {
//                groupName
//            });
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * 删除好友
//     *
//     * @param userName
//     * @return
//     */
//    public boolean removeUser(String userName) {
//        if (getConnection() == null)
//            return false;
//        try {
//            RosterEntry entry = null;
//            if (userName.contains("@"))
//                entry = getConnection().getRoster().getEntry(userName);
//            else
//                entry = getConnection().getRoster().getEntry(
//                        userName + "@" + getConnection().getServiceName());
//            if (entry == null)
//                entry = getConnection().getRoster().getEntry(userName);
//            getConnection().getRoster().removeEntry(entry);
//
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * 查询用户
//     *
//     * @param userName
//     * @return
//     * @throws XMPPException
//     */
//    public List<HashMap<String, String>> searchUsers(String userName) {
//        if (getConnection() == null)
//            return null;
//        HashMap<String, String> user = null;
//        List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
//        try {
//            new ServiceDiscoveryManager(getConnection());
//
//            UserSearchManager usm = new UserSearchManager(getConnection());
//
//            Form searchForm = usm.getSearchForm(getConnection()
//                    .getServiceName());
//            Form answerForm = searchForm.createAnswerForm();
//            answerForm.setAnswer("userAccount", true);
//            answerForm.setAnswer("userPhote", userName);
//            ReportedData data = usm.getSearchResults(answerForm, "search"
//                    + getConnection().getServiceName());
//
//            Iterator<Row> it = data.getRows();
//            Row row = null;
//            while (it.hasNext()) {
//                user = new HashMap<String, String>();
//                row = it.next();
//                user.put("userAccount", row.getValues("userAccount").next()
//                        .toString());
//                user.put("userPhote", row.getValues("userPhote").next()
//                        .toString());
//                results.add(user);
//                // 若存在，则有返回,UserName一定非空，其他两个若是有设，一定非空
//            }
//        } catch (XMPPException e) {
//            e.printStackTrace();
//        }
//        return results;
//    }
//
//    /**
//     * 修改心情
//     *
//     * @param connection
//     * @param status
//     */
//    public void changeStateMessage(String status) {
//        if (getConnection() == null)
//            return;
//        Presence presence = new Presence(Presence.Type.available);
//        presence.setStatus(status);
//        getConnection().sendPacket(presence);
//    }
//
//    /**
//     * 修改用户头像
//     *
//     * @param file
//     */
//    public boolean changeImage(File file) {
//        if (getConnection() == null)
//            return false;
//        try {
//            VCard vcard = new VCard();
//            vcard.load(getConnection());
//
//            byte[] bytes;
//
//            bytes = getFileBytes(file);
//            String encodedImage = StringUtils.encodeBase64(bytes);
//            vcard.setAvatar(bytes, encodedImage);
//            vcard.setEncodedImage(encodedImage);
//            vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"
//                    + encodedImage + "</BINVAL>", true);
//
//            ByteArrayInputStream bais = new ByteArrayInputStream(
//                    vcard.getAvatar());
//            FormatTools.getInstance().InputStream2Bitmap(bais);
//
//            vcard.save(getConnection());
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * 文件转字节
//     *
//     * @param file
//     * @return
//     * @throws IOException
//     */
//    private byte[] getFileBytes(File file) throws IOException {
//        BufferedInputStream bis = null;
//        try {
//            bis = new BufferedInputStream(new FileInputStream(file));
//            int bytes = (int) file.length();
//            byte[] buffer = new byte[bytes];
//            int readBytes = bis.read(buffer);
//            if (readBytes != buffer.length) {
//                throw new IOException("Entire file not read");
//            }
//            return buffer;
//        } finally {
//            if (bis != null) {
//                bis.close();
//            }
//        }
//    }
//
//    /**
//     * 删除当前用户
//     *
//     * @return
//     */
//    public boolean deleteAccount() {
//        if (getConnection() == null)
//            return false;
//        try {
//            getConnection().getAccountManager().deleteAccount();
//            return true;
//        } catch (XMPPException e) {
//            return false;
//        }
//    }
//
//    /**
//     * 修改密码
//     *
//     * @return
//     */
//    public boolean changePassword(String pwd) {
//        if (getConnection() == null)
//            return false;
//        try {
//            getConnection().getAccountManager().changePassword(pwd);
//            return true;
//        } catch (XMPPException e) {
//            return false;
//        }
//    }
//
//    /**
//     * 初始化会议室列表
//     */
//    public List<HostedRoom> getHostRooms() {
//        if (getConnection() == null)
//            return null;
//        Collection<HostedRoom> hostrooms = null;
//        List<HostedRoom> roominfos = new ArrayList<HostedRoom>();
//        try {
//            new ServiceDiscoveryManager(getConnection());
//            hostrooms = MultiUserChat.getHostedRooms(getConnection(),
//                    getConnection().getServiceName());
//            for (HostedRoom entry : hostrooms) {
//                roominfos.add(entry);
//                Log.i("room",
//                        "名字：" + entry.getName() + " - ID:" + entry.getJid());
//            }
//            Log.i("room", "服务会议数量:" + roominfos.size());
//        } catch (XMPPException e) {
//            e.printStackTrace();
//        }
//        return roominfos;
//    }
//
//    /**
//     * 创建房间
//     *
//     * @param roomName 房间名称
//     */
//    public MultiUserChat createRoom(String user, String roomName,
//                                    String password) {
//        if (getConnection() == null)
//            return null;
//
//        MultiUserChat muc = null;
//        try {
//            // 创建一个MultiUserChat
//            muc = new MultiUserChat(getConnection(), roomName + "@conference."
//                    + getConnection().getServiceName());
//            // 创建聊天室
//            muc.create(roomName);
//            // 获得聊天室的配置表单
//            Form form = muc.getConfigurationForm();
//            // 根据原始表单创建一个要提交的新表单。
//            Form submitForm = form.createAnswerForm();
//            // 向要提交的表单添加默认答复
//            for (Iterator<FormField> fields = form.getFields(); fields
//                    .hasNext(); ) {
//                FormField field = (FormField) fields.next();
//                if (!FormField.TYPE_HIDDEN.equals(field.getType())
//                        && field.getVariable() != null) {
//                    // 设置默认值作为答复
//                    submitForm.setDefaultAnswer(field.getVariable());
//                }
//            }
//            // 设置聊天室的新拥有者
//            List<String> owners = new ArrayList<String>();
//            owners.add(getConnection().getUser());// 用户JID
//            submitForm.setAnswer("muc#roomconfig_roomowners", owners);
//            // 设置聊天室是持久聊天室，即将要被保存下来
//            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
//            // 房间仅对成员开放
//            submitForm.setAnswer("muc#roomconfig_membersonly", false);
//            // 允许占有者邀请其他人
//            submitForm.setAnswer("muc#roomconfig_allowinvites", true);
//            if (!password.equals("")) {
//                // 进入是否需要密码
//                submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",
//                        true);
//                // 设置进入密码
//                submitForm.setAnswer("muc#roomconfig_roomsecret", password);
//            }
//            // 能够发现占有者真实 JID 的角色
//            // submitForm.setAnswer("muc#roomconfig_whois", "anyone");
//            // 登录房间对话
//            submitForm.setAnswer("muc#roomconfig_enablelogging", true);
//            // 仅允许注册的昵称登录
//            submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
//            // 允许使用者修改昵称
//            submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
//            // 允许用户注册房间
//            submitForm.setAnswer("x-muc#roomconfig_registration", false);
//            // 发送已完成的表单（有默认值）到服务器来配置聊天室
//            muc.sendConfigurationForm(submitForm);
//        } catch (XMPPException e) {
//            e.printStackTrace();
//            return null;
//        }
//        return muc;
//    }
//
//    /**
//     * 加入会议室
//     *
//     * @param user      昵称
//     * @param password  会议室密码
//     * @param roomsName 会议室名
//     */
//    public MultiUserChat joinMultiUserChat(String user, String roomsName,
//                                           String password) {
//        if (getConnection() == null)
//            return null;
//        try {
//            // 使用XMPPConnection创建一个MultiUserChat窗口
//            MultiUserChat muc = new MultiUserChat(getConnection(), roomsName
//                    + "@conference." + getConnection().getServiceName());
//            // 聊天室服务将会决定要接受的历史记录数量
//            DiscussionHistory history = new DiscussionHistory();
//            history.setMaxChars(0);
//            // history.setSince(new Date());
//            // 用户加入聊天室
//            muc.join(user, password, history,
//                    SmackConfiguration.getPacketReplyTimeout());
//            Log.i("MultiUserChat", "会议室【" + roomsName + "】加入成功........");
//            return muc;
//        } catch (XMPPException e) {
//            e.printStackTrace();
//            Log.i("MultiUserChat", "会议室【" + roomsName + "】加入失败........");
//            return null;
//        }
//    }
//
//    /**
//     * 查询会议室成员名字
//     *
//     * @param muc
//     */
//    public List<String> findMulitUser(MultiUserChat muc) {
//        if (getConnection() == null)
//            return null;
//        List<String> listUser = new ArrayList<String>();
//        Iterator<String> it = muc.getOccupants();
//        // 遍历出聊天室人员名称
//        while (it.hasNext()) {
//            // 聊天室成员名字
//            String name = StringUtils.parseResource(it.next());
//            listUser.add(name);
//        }
//        return listUser;
//    }
//
//    /**
//     * 发送文件
//     *
//     * @param user
//     * @param filePath
//     */
//    public void sendFile(String user, String filePath) {
//        if (getConnection() == null)
//            return;
//        // 创建文件传输管理器
//        FileTransferManager manager = new FileTransferManager(getConnection());
//
//        // 创建输出的文件传输
//        OutgoingFileTransfer transfer = manager
//                .createOutgoingFileTransfer(user);
//
//        // 发送文件
//        try {
//            transfer.sendFile(new File(filePath), "You won't believe this!");
//        } catch (XMPPException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 获取离线消息
//     *
//     * @return
//     */
//    public Map<String, List<HashMap<String, String>>> getHisMessage() {
//        if (getConnection() == null)
//            return null;
//        Map<String, List<HashMap<String, String>>> offlineMsgs = null;
//
//        try {
//            OfflineMessageManager offlineManager = new OfflineMessageManager(
//                    getConnection());
//            Iterator<Message> it = offlineManager.getMessages();
//
//            int count = offlineManager.getMessageCount();
//            if (count <= 0)
//                return null;
//            offlineMsgs = new HashMap<String, List<HashMap<String, String>>>();
//
//            while (it.hasNext()) {
//                Message message = it.next();
//                String fromUser = StringUtils.parseName(message.getFrom());
//                ;
//                HashMap<String, String> histrory = new HashMap<String, String>();
//                histrory.put("useraccount",
//                        StringUtils.parseName(getConnection().getUser()));
//                histrory.put("friendaccount", fromUser);
//                histrory.put("info", message.getBody());
//                histrory.put("type", "left");
//                if (offlineMsgs.containsKey(fromUser)) {
//                    offlineMsgs.get(fromUser).add(histrory);
//                } else {
//                    List<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();
//                    temp.add(histrory);
//                    offlineMsgs.put(fromUser, temp);
//                }
//            }
//            offlineManager.deleteMessages();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return offlineMsgs;
//    }
//
//    /**
//     * 判断OpenFire用户的状态 strUrl :
//     * url格式 - http://my.openfire.com:9090/plugins/presence
//     * /status?jid=user1@SERVER_NAME&type=xml
//     * 返回值 : 0 - 用户不存在; 1 - 用户在线; 2 - 用户离线
//     * 说明 ：必须要求 OpenFire加载 presence 插件，同时设置任何人都可以访问
//     */
//    public int IsUserOnLine(String user) {
//        String url = "http://" + SERVER_HOST + ":9090/plugins/presence/status?" +
//                "jid=" + user + "@" + SERVER_NAME + "&type=xml";
//        int shOnLineState = 0; // 不存在
//        try {
//            URL oUrl = new URL(url);
//            URLConnection oConn = oUrl.openConnection();
//            if (oConn != null) {
//                BufferedReader oIn = new BufferedReader(new InputStreamReader(
//                        oConn.getInputStream()));
//                if (null != oIn) {
//                    String strFlag = oIn.readLine();
//                    oIn.close();
//                    System.out.println("strFlag" + strFlag);
//                    if (strFlag.indexOf("type=\"unavailable\"") >= 0) {
//                        shOnLineState = 2;
//                    }
//                    if (strFlag.indexOf("type=\"error\"") >= 0) {
//                        shOnLineState = 0;
//                    } else if (strFlag.indexOf("priority") >= 0
//                            || strFlag.indexOf("id=\"") >= 0) {
//                        shOnLineState = 1;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return shOnLineState;
//    }
//
//    /**
//     * 加入providers的函数 ASmack在/META-INF缺少一个smack.providers 文件
//     *
//     * @param pm
//     */
//    public void configureConnection(ProviderManager pm) {
//
//        // Private Data Storage
//        pm.addIQProvider("query", "jabber:iq:private",
//                new PrivateDataManager.PrivateDataIQProvider());
//
//        // Time
//        try {
//            pm.addIQProvider("query", "jabber:iq:time",
//                    Class.forName("org.jivesoftware.smackx.packet.Time"));
//        } catch (ClassNotFoundException e) {
//            Log.w("TestClient",
//                    "Can't load class for org.jivesoftware.smackx.packet.Time");
//        }
//
//        // Roster Exchange
//        pm.addExtensionProvider("x", "jabber:x:roster",
//                new RosterExchangeProvider());
//
//        // Message Events
//        pm.addExtensionProvider("x", "jabber:x:event",
//                new MessageEventProvider());
//
//        // Chat State
//        pm.addExtensionProvider("active",
//                "http://jabber.org/protocol/chatstates",
//                new ChatStateExtension.Provider());
//        pm.addExtensionProvider("composing",
//                "http://jabber.org/protocol/chatstates",
//                new ChatStateExtension.Provider());
//        pm.addExtensionProvider("paused",
//                "http://jabber.org/protocol/chatstates",
//                new ChatStateExtension.Provider());
//        pm.addExtensionProvider("inactive",
//                "http://jabber.org/protocol/chatstates",
//                new ChatStateExtension.Provider());
//        pm.addExtensionProvider("gone",
//                "http://jabber.org/protocol/chatstates",
//                new ChatStateExtension.Provider());
//
//        // XHTML
//        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
//                new XHTMLExtensionProvider());
//
//        // Group Chat Invitations
//        pm.addExtensionProvider("x", "jabber:x:conference",
//                new GroupChatInvitation.Provider());
//
//        // Service Discovery # Items
//        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
//                new DiscoverItemsProvider());
//
//        // Service Discovery # Info
//        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
//                new DiscoverInfoProvider());
//
//        // Data Forms
//        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
//
//        // MUC User
//        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
//                new MUCUserProvider());
//
//        // MUC Admin
//        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
//                new MUCAdminProvider());
//
//        // MUC Owner
//        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
//                new MUCOwnerProvider());
//
//        // Delayed Delivery
//        pm.addExtensionProvider("x", "jabber:x:delay",
//                new DelayInformationProvider());
//
//        // Version
//        try {
//            pm.addIQProvider("query", "jabber:iq:version",
//                    Class.forName("org.jivesoftware.smackx.packet.Version"));
//        } catch (ClassNotFoundException e) {
//            // Not sure what's happening here.
//        }
//
//        // VCard
//        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
//
//        // Offline Message Requests
//        pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
//                new OfflineMessageRequest.Provider());
//
//        // Offline Message Indicator
//        pm.addExtensionProvider("offline",
//                "http://jabber.org/protocol/offline",
//                new OfflineMessageInfo.Provider());
//
//        // Last Activity
//        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
//
//        // User Search
//        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
//
//        // SharedGroupsInfo
//        pm.addIQProvider("sharedgroup",
//                "http://www.jivesoftware.org/protocol/sharedgroup",
//                new SharedGroupsInfo.Provider());
//
//        // JEP-33: Extended Stanza Addressing
//        pm.addExtensionProvider("addresses",
//                "http://jabber.org/protocol/address",
//                new MultipleAddressesProvider());
//
//        // FileTransfer
//        pm.addIQProvider("si", "http://jabber.org/protocol/si",
//                new StreamInitiationProvider());
//
//        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
//                new BytestreamsProvider());
//
//        // Privacy
//        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
//        pm.addIQProvider("command", "http://jabber.org/protocol/commands",
//                new AdHocCommandDataProvider());
//        pm.addExtensionProvider("malformed-action",
//                "http://jabber.org/protocol/commands",
//                new AdHocCommandDataProvider.MalformedActionError());
//        pm.addExtensionProvider("bad-locale",
//                "http://jabber.org/protocol/commands",
//                new AdHocCommandDataProvider.BadLocaleError());
//        pm.addExtensionProvider("bad-payload",
//                "http://jabber.org/protocol/commands",
//                new AdHocCommandDataProvider.BadPayloadError());
//        pm.addExtensionProvider("bad-sessionid",
//                "http://jabber.org/protocol/commands",
//                new AdHocCommandDataProvider.BadSessionIDError());
//        pm.addExtensionProvider("session-expired",
//                "http://jabber.org/protocol/commands",
//                new AdHocCommandDataProvider.SessionExpiredError());
//    }
//
//}
//
//    /**
//     * 判断OpenFire用户的状态 strUrl :
//     * url格式 - http://my.openfire.com:9090/plugins/presence
//     * /status?jid=user1@SERVER_NAME&type=xml
//     * 返回值 : 0 - 用户不存在; 1 - 用户在线; 2 - 用户离线
//     * 说明 ：必须要求 OpenFire加载 presence 插件，同时设置任何人都可以访问
//     */
//    public int IsUserOnLine(String user) {
//        String url = "http://" + SERVER_HOST + ":9090/plugins/presence/status?" +
//                "jid=" + user + "@" + SERVER_NAME + "&type=xml";
//        int shOnLineState = 0; // 不存在
//        try {
//            URL oUrl = new URL(url);
//            URLConnection oConn = oUrl.openConnection();
//            if (oConn != null) {
//                BufferedReader oIn = new BufferedReader(new InputStreamReader(
//                        oConn.getInputStream()));
//                if (null != oIn) {
//                    String strFlag = oIn.readLine();
//                    oIn.close();
//                    System.out.println("strFlag" + strFlag);
//                    if (strFlag.indexOf("type=\"unavailable\"") >= 0) {
//                        shOnLineState = 2;
//                    }
//                    if (strFlag.indexOf("type=\"error\"") >= 0) {
//                        shOnLineState = 0;
//                    } else if (strFlag.indexOf("priority") >= 0
//                            || strFlag.indexOf("id=\"") >= 0) {
//                        shOnLineState = 1;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return shOnLineState;
//    }
