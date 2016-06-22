package hydrocoolapps.waterwise.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import hydrocoolapps.waterwise.R;

public class SearchDialogFragment extends DialogFragment {

    private TextInputLayout inputLayoutSearch;
    private EditText mEditText;
    private Button btnSearch, btnCancel;

    public SearchDialogFragment() {
        //Empty
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_dialog, container);

        inputLayoutSearch = (TextInputLayout) view.findViewById(R.id.input_layout_search);
        mEditText = (EditText) view.findViewById(R.id.input_search);
        btnSearch = (Button) view.findViewById(R.id.btn_search);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent()
                        .putExtra("search", mEditText.getText().toString());

                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                mEditText.getText().clear();
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mEditText.getText().clear();
                dismiss();
            }
        });
        
        return view;
    }

}
