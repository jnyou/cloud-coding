/**
  * Copyright 2021 json.cn 
  */
package io.commchina.http.resp;
import lombok.Data;

import java.util.Date;

/**
 * Auto-generated: 2021-06-18 14:24:9
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class GithubUser {

    private String login;
    private int id;
    private String nodeId;
    private String avatarUrl;
    private String gravatarId;
    private String url;
    private String htmlUrl;
    private String followersUrl;
    private String followingUrl;
    private String gistsUrl;
    private String starredUrl;
    private String subscriptionsUrl;
    private String organizationsUrl;
    private String reposUrl;
    private String eventsUrl;
    private String receivedEventsUrl;
    private String type;
    private boolean siteAdmin;
    private String name;
    private String company;
    private String blog;
    private String location;
    private String email;
    private boolean hireable;
    private String bio;
    private String twitterUsername;
    private int publicRepos;
    private int publicGists;
    private int followers;
    private int following;
    private Date createdAt;
    private Date updatedAt;
    private int privateGists;
    private int totalPrivateRepos;
    private int ownedPrivateRepos;
    private int diskUsage;
    private int collaborators;
    private boolean twoFactorAuthentication;
    private Plan plan;
}


/**
 * Auto-generated: 2021-06-18 14:24:9
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
class Plan {

    private String name;
    private int space;
    private int privateRepos;
    private int collaborators;
}