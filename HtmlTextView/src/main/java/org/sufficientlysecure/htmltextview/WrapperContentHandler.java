/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sufficientlysecure.htmltextview;

import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class WrapperContentHandler implements ContentHandler, Html.TagHandler {
    private ContentHandler mContentHandler;
    private WrapperTagHandler mTagHandler;
    private Editable mSpannableStringBuilder;
    private OnImageClickListener onImageClickListener;
    public WrapperContentHandler(WrapperTagHandler tagHandler) {
        this.mTagHandler = tagHandler;
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener){
        this.onImageClickListener=onImageClickListener;
    }
    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if(tag.equalsIgnoreCase("img")){
            int len = output.length();
            ImageSpan[] images = output.getSpans(len-1, len, ImageSpan.class);
            String imgURL = images[0].getSource();
            output.setSpan(new ClickableImage(imgURL), len-1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (mContentHandler == null) {
            mSpannableStringBuilder = output;
            mContentHandler = xmlReader.getContentHandler();
            xmlReader.setContentHandler(this);
        }
    }
    private class ClickableImage extends ClickableSpan {

        private String url;

        public ClickableImage(String url) {
            this.url = url;
        }

        @Override
        public void onClick(@NonNull View view) {
            onImageClickListener.onClick(url);
        }
    }
    @Override
    public void setDocumentLocator(Locator locator) {
        mContentHandler.setDocumentLocator(locator);
    }

    @Override
    public void startDocument() throws SAXException {
        mContentHandler.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        mContentHandler.endDocument();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        mContentHandler.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        mContentHandler.endPrefixMapping(prefix);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (!mTagHandler.handleTag(true, localName, mSpannableStringBuilder, attributes)) {
            mContentHandler.startElement(uri, localName, qName, attributes);
        }
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!mTagHandler.handleTag(false, localName, mSpannableStringBuilder, null)) {
            mContentHandler.endElement(uri, localName, qName);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        mContentHandler.characters(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        mContentHandler.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        mContentHandler.processingInstruction(target, data);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        mContentHandler.skippedEntity(name);
    }

}
