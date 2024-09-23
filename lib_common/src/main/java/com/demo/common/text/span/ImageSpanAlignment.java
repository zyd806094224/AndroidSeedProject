package com.demo.common.text.span;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Description:
 * @Date: 2023/8/2 11:51 AM
 * 
 * @version: 1.0
 */
@IntDef({ImgSpan.ALIGN_BOTTOM, ImgSpan.ALIGN_BASELINE, ImgSpan.ALIGN_CENTER})
@Retention(RetentionPolicy.SOURCE)
public @interface ImageSpanAlignment {
}


