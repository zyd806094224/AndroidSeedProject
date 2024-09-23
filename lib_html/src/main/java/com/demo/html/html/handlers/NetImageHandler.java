/*
 * Copyright (C) 2011 Alex Kuiper <http://www.nightwhistler.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.demo.html.html.handlers;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import com.demo.html.html.utils.HtmlHttpImageGetter;
import com.demo.html.html.utils.SpanStack;
import com.demo.html.htmlcleaner.TagNode;


/**
 * Handles image tags.
 * <p>
 * The default implementation tries to load images through a URL.openStream(),
 * override loadBitmap() to implement your own loading.
 *
 * @author Alex Kuiper
 */
public class NetImageHandler extends ImageHandler {

    Html.ImageGetter img;

    public NetImageHandler(Html.ImageGetter img) {
        this.img = img;
    }

    @Override
    public void handleTagNode(TagNode node, SpannableStringBuilder builder,
                              int start, int end, SpanStack stack) {
        String src = node.getAttributeByName("src");
        String widthStr = node.getAttributeByName("width");
        String heightStr = node.getAttributeByName("height");
        builder.append("\uFFFC");
        Drawable d = null;
        int width;
        int height;
        try {
            width = Integer.parseInt(widthStr);
            height = Integer.parseInt(heightStr);
        } catch (Exception e) {
            width = 0;
            height = 0;
            e.printStackTrace();
        }
        if (img != null) {
            if (img instanceof HtmlHttpImageGetter) {
                d = ((HtmlHttpImageGetter) img).getDrawable(src, width, height);
            } else {
                d = img.getDrawable(src);
            }
        }
        stack.pushSpan(new ImageSpan(d, src, DynamicDrawableSpan.ALIGN_BOTTOM), start, builder.length());
    }

}
