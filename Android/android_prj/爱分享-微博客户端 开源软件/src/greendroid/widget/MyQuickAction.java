/*
 * Copyright (C) 2010 Cyril Mottier (http://www.cyrilmottier.com)
 *
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
package greendroid.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;

public class MyQuickAction extends QuickAction {
    private static final ColorFilter BLACK_CF = new LightingColorFilter(Color.BLACK, Color.BLACK);

    public MyQuickAction(Context ctx, int drawableId, int titleId) {
        super(ctx, buildDrawable(ctx, drawableId), titleId);
    }
    
    public static Drawable buildDrawable(Context ctx, int drawableId) {
        Drawable d = ctx.getResources().getDrawable(drawableId);
        d.setColorFilter(BLACK_CF);
        return d;
    }
}
