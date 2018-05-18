package com.viginfotech.chennaitimes.backend.tamil;


import com.viginfotech.chennaitimes.backend.Constants;
import com.viginfotech.chennaitimes.backend.model.Feed;
import com.viginfotech.chennaitimes.backend.utils.TimeUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by anand on 8/30/15.
 */
public class NakkheeranParser {
    private static final String TAG = NakkheeranParser.class.getSimpleName();

    public static List<Feed> parseFeed(String content, int category) {

        content = content != null ? content.replaceAll("&", "&amp;") : "";
        content = content != null ? content.replaceAll("%3F", "&#63;") : "";

        try {
            boolean inDataItemTag = false;
            String currentTagName = "";
            Feed newsItem = null;
            List<Feed> newsList = new ArrayList<>();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(content));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTagName = parser.getName();
                        if (currentTagName.equals("item")) {
                            inDataItemTag = true;
                            newsItem = new Feed();
                            newsItem.setCategoryId(category);
                            newsItem.setSourceId(Constants.SOURCE_NAKKHEERAN);
                            newsList.add(newsItem);
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("item")) {
                            inDataItemTag = false;
                        }
                        currentTagName = "";
                        break;

                    case XmlPullParser.TEXT:
                        if (inDataItemTag && newsItem != null) {
                            switch (currentTagName) {
                                case "title":
                                    newsItem.setTitle(parser.getText());

                                    break;
                                case "link":
                                    newsItem.setGuid(parser.getText().trim());
                                    break;

                                case "pubDate":
                                    long now = System.currentTimeMillis();
                                    String parserTime = parser.getText();
                                    Date date = TimeUtils.parseTimeStamp(parserTime, Constants.SOURCE_NAKKHEERAN);
                                    if (date != null)
                                        newsItem.setPubDate(date.getTime());
                                    else newsItem.setPubDate(now);


                                    break;

                                default:
                                    break;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }


            return newsList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}


