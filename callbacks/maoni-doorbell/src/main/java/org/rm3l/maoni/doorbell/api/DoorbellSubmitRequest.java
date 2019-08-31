package org.rm3l.maoni.doorbell.api;

import java.util.List;
import java.util.Map;

/**
 * Request class for Doorbell's /submit API.
 * <p>@see https://doorbell.io/docs/api</p>
 */
public class DoorbellSubmitRequest {

  /**
   * The email address of the user sending feedback
   */
  private String email;

  /**
   * The feedback message the user has entered
   */
  private String message;

  /**
   * The name of the user who is sending feedback
   */
  private String name;

  /**
   * The IP address of the end user, so your server's IP address isn't the one saved
   */
  private String ip;

  /**
   * The sentiment of the message, if you don't want this analyzed automatically.
   * The possible values are: positive, neutral, or negative
   */
  private Sentiment sentiment;

  /**
   * JSON encoded array of strings, or a comma separated string of tags you want to add to the feedback
   */
  private List<String> tags;

  /**
   * JSON encoded string of properties (of any data type), any extra metadata you want to attach to the message
   */
  private Map<String, Object> properties;

  /**
   * Array of attachment IDs (get these from uploading to the upload endpoint
   */
  private List<Long> attachments;

  /**
   * The NPS rating the user submitted alongside the message
   */
  private Integer nps;

  /**
   * The language the site/app is using. The response messages will be in the corresponding language, if translated
   */
  private String language;

  public String getEmail() {
    return email;
  }

  public DoorbellSubmitRequest setEmail(final String email) {
    this.email = email;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public DoorbellSubmitRequest setMessage(final String message) {
    this.message = message;
    return this;
  }

  public String getName() {
    return name;
  }

  public DoorbellSubmitRequest setName(final String name) {
    this.name = name;
    return this;
  }

  public String getIp() {
    return ip;
  }

  public DoorbellSubmitRequest setIp(final String ip) {
    this.ip = ip;
    return this;
  }

  public Sentiment getSentiment() {
    return sentiment;
  }

  public DoorbellSubmitRequest setSentiment(
      final Sentiment sentiment) {
    this.sentiment = sentiment;
    return this;
  }

  public List<String> getTags() {
    return tags;
  }

  public DoorbellSubmitRequest setTags(final List<String> tags) {
    this.tags = tags;
    return this;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public DoorbellSubmitRequest setProperties(final Map<String, Object> properties) {
    this.properties = properties;
    return this;
  }

  public List<Long> getAttachments() {
    return attachments;
  }

  public DoorbellSubmitRequest setAttachments(final List<Long> attachments) {
    this.attachments = attachments;
    return this;
  }

  public Integer getNps() {
    return nps;
  }

  public DoorbellSubmitRequest setNps(final Integer nps) {
    this.nps = nps;
    return this;
  }

  public String getLanguage() {
    return language;
  }

  public DoorbellSubmitRequest setLanguage(final String language) {
    this.language = language;
    return this;
  }

  public enum Sentiment {
    positive,
    neutral,
    negative
  }


}
