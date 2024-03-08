package com.example.scandroid;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Generates a profile picture for when users remove their picture or do not have one yet
 */
//OpenAI, 2024, ChatGPT, Create a bitmap of letter using canvas and paint
public class ProfilePictureGenerator {
    /**
     * Generate the bitmap of the profile picture based on the user's name
     * @param name The user's name
     * @return
     * A Bitmap profile picture
     */
    public Bitmap generatePictureBitmap(String name) {
        // Create a new Bitmap with the specified width and height
        int width = 200;
        int height = 200;
        name = name.toLowerCase();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // Create a Canvas to draw on the Bitmap
        Canvas canvas = new Canvas(bitmap);

       char firstLetter = name.charAt(0);
        int backgroundColor = generateColor(firstLetter, "background");
        canvas.drawColor(backgroundColor);

        // Create a Paint object for drawing text
        Paint paint = new Paint();
        paint.setColor(generateColor(name.charAt(name.length() - 1), "letter"));
        paint.setTextSize(250);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        // Generate a random letter

        // Calculate the position to center the letter
        float x = (width - paint.measureText(String.valueOf(firstLetter))) / 2;
        float y = (height - paint.ascent() - paint.descent()) / 2;

        // Draw the random letter on the canvas
        canvas.drawText(String.valueOf(firstLetter), x, y-30, paint);

        return bitmap;
    }

    /**
     * Deterministically generate a color for the letter and background of profile picture
     * @param firstLetter First letter of user's name used to determine colors
     * @param type Determine if generating a color for the background or for the letter itself
     * @return
     * A Color
     */
    private static int generateColor(char firstLetter, String type) {
        if (type == "background"){
        if ('a' <=firstLetter && firstLetter <= 'i'){
            return Color.rgb(255,0,0);
        }
        else if ('i' <firstLetter && firstLetter <= 'q'){
            return Color.rgb(0,255,0);
        }
        else if ('q' < firstLetter){
            return Color.rgb(0,0,255);
        }
        else {
            return Color.rgb(250,0,0);
        }
    } else {
            if ('a' <=firstLetter && firstLetter <= 'i'){
                return Color.rgb(10,0,200);
            }
            else if ('i' <firstLetter && firstLetter <= 'q'){
                return Color.rgb(20,50,100);
            }
            else if ('q' < firstLetter){
                return Color.rgb(175,75,25);
            }
            else {
                return Color.rgb(40,120,175);
            }
        }
    }
}
