package com.part3.team07.sb01deokhugamteam07.client.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@XmlRootElement(name = "rss")
@XmlAccessorType(XmlAccessType.FIELD)
public class NaverBookRssResponse {

  private Channel channel;

  @XmlAccessorType(XmlAccessType.FIELD)
  public static class Channel {
    @XmlElement(name = "item")
    private List<Item> items;
  }

  @Getter
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class Item {
    private String title;
    private String author;
    private String description;
    private String publisher;
    private String pubdate;
    private String isbn;
    private String image;
  }

  public List<Item> getItems() {
    return channel != null ? channel.items : Collections.emptyList();
  }
}
