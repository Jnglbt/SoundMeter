package com.soundmeterpl.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.soundmeterpl.R;

public class InfoDialog extends AppCompatDialog
{
    public InfoDialog(Context context)
    {
        super(context);
    }

    public InfoDialog(Context context, int theme)
    {
        super(context, theme);
    }

    public static class Builder
    {
        private Context context;
        private String title;
        private String message;
        private String negativeButtonText;
        private View contentView;
        private DialogInterface.OnClickListener negativeButtonClickListener;

        public Builder(Context context)
        {
            this.context = context;
        }

        public Builder setMessage(String message)
        {
            this.message = message;
            return this;
        }

        public Builder setMessage(int message)
        {
            this.message = (String) context.getText(message);
            return this;
        }

        public Builder setTitle(int title)
        {
            this.title = (String) context.getText(title);
            return this;
        }

        public Builder setTitle(String title)
        {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v)
        {
            this.contentView = v;
            return this;
        }


        public Builder setNegativeButton(int negativeButtonText,
                                         DialogInterface.OnClickListener listener)
        {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         DialogInterface.OnClickListener listener)
        {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public InfoDialog create()
        {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final InfoDialog dialog = new InfoDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_info, null);
            dialog.addContentView(layout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            // set the cancel button
            if (negativeButtonText != null)
            {
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null)
                {
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener()
                            {
                                public void onClick(View v)
                                {
                                    negativeButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else
            {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }
            // set the content message
            if (message != null)
            {
                //((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null)
            {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }
}
