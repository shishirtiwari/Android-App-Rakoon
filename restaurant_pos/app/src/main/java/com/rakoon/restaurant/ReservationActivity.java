package com.rakoon.restaurant;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rakoon.restaurant.model.Branch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ReservationActivity extends AppCompatActivity {
    @BindView(R.id.book)
    TextView book;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.openinghours)
    TextView openinghours;
    @BindView(R.id.firstname)
    EditText firstname;
    @BindView(R.id.lastname)
    EditText lastname;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.mobileno)
    EditText mobileno;
    @BindView(R.id.peopletext)
    TextView peopletext;
    @BindView(R.id.people)
    Spinner people;
    @BindView(R.id.branchtext)
    TextView branchtext;
    @BindView(R.id.branch)
    Spinner branch;
    @BindView(R.id.time)
    TextView timetv;
    @BindView(R.id.date)
    TextView datetv;
    @BindView(R.id.selectdateandtime)
    TextView selectdateandtime;
    @BindView(R.id.mainlay)
    ConstraintLayout mainlay;
    @BindView(R.id.firstnameinput)
    TextInputLayout firstnameinput;
    @BindView(R.id.lastnameinput)
    TextInputLayout lastnameinput;
    @BindView(R.id.emailinput)
    TextInputLayout emailinput;
    @BindView(R.id.mobilenoinput)
    TextInputLayout mobilenoinput;

    AllPath apath;
    Unbinder unbinder;
    ArrayList<Branch> branchlist;
    ArrayList<String> peoplelist;
    AlertDialog alertDialog;

    private SimpleDateFormat inputParser;
    private Date date;
    private Date dateCompareOne;
    private Date dateCompareTwo;

    String str_firstname = "", str_lastname = "", str_email = "", str_mobileno = "", str_branch = "", str_person = "", str_date = "", str_time = "";
    String reservation;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        unbinder = ButterKnife.bind(this);
        apath = new AllPath();
        reservation = getIntent().getStringExtra("reservation");
        if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
            title.setText(getString(R.string.booktable_en));
//            firstname.setHint(getString(R.string.firstname_en));
//            lastname.setHint(getString(R.string.lastname_en));
//            email.setHint(getString(R.string.email_en));
//            mobileno.setHint(getString(R.string.mobileno_en));

            book.setText(getString(R.string.booktable_en));
            openinghours.setText(getString(R.string.openinghours_en));
            peopletext.setHint(getString(R.string.people_en));
            branchtext.setHint(getString(R.string.branch_en));
            selectdateandtime.setHint(getString(R.string.selectdatetime_en));

            firstnameinput.setHint(getString(R.string.firstname_en));
            lastnameinput.setHint(getString(R.string.lastname_en));
            emailinput.setHint(getString(R.string.email_en));
            mobilenoinput.setHint(getString(R.string.mobileno_en));

        } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            title.setText(getString(R.string.booktable_ar));
//            firstname.setHint(getString(R.string.firstname_ar));
//            lastname.setHint(getString(R.string.lastname_ar));
//            email.setHint(getString(R.string.email_ar));
//            mobileno.setHint(getString(R.string.mobileno_ar));
            book.setText(getString(R.string.booktable_ar));
            openinghours.setText(getString(R.string.openinghours_ar));
            peopletext.setHint(getString(R.string.people_ar));
            branchtext.setHint(getString(R.string.branch_ar));
            selectdateandtime.setHint(getString(R.string.selectdatetime_ar));

            firstnameinput.setHint(getString(R.string.firstname_ar));
            lastnameinput.setHint(getString(R.string.lastname_ar));
            emailinput.setHint(getString(R.string.email_ar));
            mobilenoinput.setHint(getString(R.string.mobileno_ar));
        }
        branchlist = new ArrayList<>();
        peoplelist = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            if (i == 1) {
                if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    peoplelist.add(i + " " + getString(R.string.person_en));
                } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    peoplelist.add(i + " " + getString(R.string.person_ar));
                }
            } else {
                if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                    peoplelist.add(i + " " + getString(R.string.people_en));
                } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                    peoplelist.add(i + " " + getString(R.string.people_ar));
                }
            }
        }
        people.setAdapter(new Peopleadap());
        if (apath.isInternetOn(ReservationActivity.this))
            getBranchLocation();
        else {
            String msg = "", str_action = "";
            if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                msg = getResources().getString(R.string.internet_message_en);
                str_action = getResources().getString(R.string.enable_en);
            } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                msg = getResources().getString(R.string.internet_message_ar);
                str_action = getResources().getString(R.string.enable_ar);
            }

            Snackbar snackbar = Snackbar.make(mainlay, msg, Snackbar.LENGTH_LONG)
                    .setAction(str_action, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // put your logic here
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                        }
                    });

            snackbar.show();
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    finish();
                }
            });
        }
        branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (branchlist.size() > i) {
                    Branch br = branchlist.get(i);
                    if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                        openinghours.setText(getString(R.string.openinghours_en) + " " + br.getOpen() + "-" + br.getClose());
                    } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                        openinghours.setText(getString(R.string.openinghours_ar) + " " + br.getOpen() + "-" + br.getClose());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        SimpleDateFormat formatdate = new SimpleDateFormat("yyyy, MMM-dd");//hh:mm:ss a
        SimpleDateFormat formattime = new SimpleDateFormat("hh:mm a");
        Calendar date = Calendar.getInstance();
        datetv.setText(formatdate.format(date.getTime()));
        timetv.setText(formattime.format(date.getTime()));

        datetv.setOnClickListener((view -> {
            showDateTimePicker();
        }));
        timetv.setOnClickListener((view -> {
            showDateTimePicker();
        }));

        book.setOnClickListener((v) ->
        {
            if (reservationValidation()) {
                if (apath.isInternetOn(ReservationActivity.this))
                    bookTable();
                else {
                    String msg = "", str_action = "";
                    if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                        msg = getResources().getString(R.string.internet_message_en);
                        str_action = getResources().getString(R.string.enable_en);
                    } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                        msg = getResources().getString(R.string.internet_message_ar);
                        str_action = getResources().getString(R.string.enable_ar);
                    }

                    Snackbar.make(mainlay, msg, Snackbar.LENGTH_LONG)
                            .setAction(str_action, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // put your logic here
                                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
            }
        });
        if (apath.isInternetOn(ReservationActivity.this))
            getUserInfo();
        else {
            String msg = "", str_action = "";
            if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                msg = getResources().getString(R.string.internet_message_en);
                str_action = getResources().getString(R.string.enable_en);
            } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                msg = getResources().getString(R.string.internet_message_ar);
                str_action = getResources().getString(R.string.enable_ar);
            }

            Snackbar.make(mainlay, msg, Snackbar.LENGTH_LONG)
                    .setAction(str_action, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // put your logic here
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .show();
        }

    }

    boolean reservationValidation() {
        str_firstname = firstname.getText().toString();
        str_lastname = lastname.getText().toString();
        str_email = email.getText().toString();
        str_mobileno = mobileno.getText().toString();
        String opentime = "", closetime = "";

        if (branchlist.size() > 0) {
            str_branch = branchlist.get(branch.getSelectedItemPosition()).getId();
            opentime = branchlist.get(branch.getSelectedItemPosition()).getOpen();
            closetime = branchlist.get(branch.getSelectedItemPosition()).getClose();
        }
        /// COdrelgfhhhhhhhhh  hgfffffffffffffffffff
        if (peoplelist.size() > 0)
            str_person = peoplelist.get(people.getSelectedItemPosition());
        str_date = datetv.getText().toString();
        str_time = timetv.getText().toString();


      /*  if (str_usertype.equalsIgnoreCase("0")) {
            Snackbar.make(mainlay, "Please select user type", Snackbar.LENGTH_LONG).show();
            return false;
        } else*/
        if (str_firstname.equalsIgnoreCase("")) {
            firstname.requestFocus();
            firstname.setBackgroundResource(R.drawable.bottom_line_red);
            firstname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.firstname_valid_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.firstname_valid_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (str_lastname.equalsIgnoreCase("")) {
            lastname.requestFocus();
            firstname.setBackgroundResource(R.drawable.bottomline_selector);
            firstname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            lastname.setBackgroundResource(R.drawable.bottom_line_red);
            lastname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.lastname_valid_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.lastname_valid_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (str_email.equalsIgnoreCase("")) {
            email.requestFocus();
            firstname.setBackgroundResource(R.drawable.bottomline_selector);
            firstname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            lastname.setBackgroundResource(R.drawable.bottomline_selector);
            lastname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            email.setBackgroundResource(R.drawable.bottom_line_red);
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.email_valid_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.email_valid_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (!apath.isValidEmail(str_email)) {
            email.requestFocus();
            firstname.setBackgroundResource(R.drawable.bottomline_selector);
            firstname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            lastname.setBackgroundResource(R.drawable.bottomline_selector);
            lastname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            email.setBackgroundResource(R.drawable.bottom_line_red);
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.email_correct_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.email_correct_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (str_mobileno.equalsIgnoreCase("")) {
            mobileno.requestFocus();
            firstname.setBackgroundResource(R.drawable.bottomline_selector);
            firstname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            lastname.setBackgroundResource(R.drawable.bottomline_selector);
            lastname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            email.setBackgroundResource(R.drawable.bottomline_selector);
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottom_line_red);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.mobileno_msg_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.mobileno_msg_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (!str_mobileno.matches(apath.MobilePattern)) {
            mobileno.requestFocus();
            firstname.setBackgroundResource(R.drawable.bottomline_selector);
            firstname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            lastname.setBackgroundResource(R.drawable.bottomline_selector);
            lastname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            email.setBackgroundResource(R.drawable.bottomline_selector);
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottom_line_red);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error, 0);
            if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.mobileno_valid_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.mobileno_valid_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (str_person.equalsIgnoreCase("")) {
            firstname.setBackgroundResource(R.drawable.bottomline_selector);
            firstname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            lastname.setBackgroundResource(R.drawable.bottomline_selector);
            lastname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            email.setBackgroundResource(R.drawable.bottomline_selector);
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottomline_selector);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.select_people_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.select_people_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (str_branch.equalsIgnoreCase("")) {
            firstname.setBackgroundResource(R.drawable.bottomline_selector);
            firstname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            lastname.setBackgroundResource(R.drawable.bottomline_selector);
            lastname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            email.setBackgroundResource(R.drawable.bottomline_selector);
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottomline_selector);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.select_branch_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.select_branch_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (str_date.equalsIgnoreCase("")) {
            firstname.setBackgroundResource(R.drawable.bottomline_selector);
            firstname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            lastname.setBackgroundResource(R.drawable.bottomline_selector);
            lastname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            email.setBackgroundResource(R.drawable.bottomline_selector);
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottomline_selector);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.select_date_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.select_date_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (str_time.equalsIgnoreCase("")) {
            firstname.setBackgroundResource(R.drawable.bottomline_selector);
            firstname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            lastname.setBackgroundResource(R.drawable.bottomline_selector);
            lastname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            email.setBackgroundResource(R.drawable.bottomline_selector);
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottomline_selector);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                Snackbar.make(mainlay, getString(R.string.select_time_en), Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                Snackbar.make(mainlay, getString(R.string.select_time_ar), Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else if (openCloseValidation(opentime, closetime, str_time)) {
            if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english))) {
                String msg = getResources().getString(R.string.close_msg_en) + " " + opentime + " - " + closetime;
                Snackbar.make(mainlay, msg, Snackbar.LENGTH_LONG).show();
            } else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
                String msg = getResources().getString(R.string.close_msg_ar) + " " + opentime + " - " + closetime;
                Snackbar.make(mainlay, msg, Snackbar.LENGTH_LONG).show();
            }
            return false;
        } else {
            apath.hideKeyboard(ReservationActivity.this);
            firstname.setBackgroundResource(R.drawable.bottomline_selector);
            firstname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            lastname.setBackgroundResource(R.drawable.bottomline_selector);
            lastname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            email.setBackgroundResource(R.drawable.bottomline_selector);
            email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mobileno.setBackgroundResource(R.drawable.bottomline_selector);
            mobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            return true;
        }
    }

    boolean openCloseValidation(String opentime, String closetime, String str_time) {
        Log.e("time", opentime + "  ,,  " + closetime + " ,, " + str_time);
        String inputFormat = "hh:mm a";
        inputParser = new SimpleDateFormat(inputFormat, Locale.US);
      /*  if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic))) {
            //Locale locale = new Locale("ar");
            opentime.replace(getResources().getString(R.string.am_ar), "AM");
            opentime.replace(getResources().getString(R.string.pm_ar), "PM");
            closetime.replace(getResources().getString(R.string.am_ar), "AM");
            closetime.replace(getResources().getString(R.string.pm_ar), "PM");
            inputParser = new SimpleDateFormat(inputFormat, Locale.US);
            Date currDate = new Date();
            String formattedDate = inputParser.format(currDate);
            str_time = formattedDate;
            Log.e("ARABIC..", formattedDate);
        }*/
        Log.e("open close current", opentime + " ,, " + closetime + " ,, " + str_time);
        date = parseDate(str_time);
        dateCompareOne = parseDate(opentime);
        dateCompareTwo = parseDate(closetime);
        if (date.after(dateCompareOne) && date.before(dateCompareTwo)) {
            return false;
        } else {
            return true;
        }
    }

    private Date parseDate(String date) {

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    class Peopleadap extends BaseAdapter {

        @Override
        public int getCount() {
            return peoplelist.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View vw = getLayoutInflater().inflate(R.layout.textview, null);
            TextView tvt = vw.findViewById(R.id.text1);
            tvt.setText(peoplelist.get(i));
            int padding = (int) getResources().getDimension(R.dimen._8sdp);
            int padding5 = (int) getResources().getDimension(R.dimen._4sdp);
            tvt.setPadding(padding5, padding, padding5, padding);
            return vw;
        }
    }

    class Branchadap extends BaseAdapter {

        @Override
        public int getCount() {
            return branchlist.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View vw = getLayoutInflater().inflate(R.layout.textview, null);
            TextView tvt = vw.findViewById(R.id.text1);
            tvt.setText(branchlist.get(i).getName());
            int padding = (int) getResources().getDimension(R.dimen._8sdp);
            int padding5 = (int) getResources().getDimension(R.dimen._4sdp);
            tvt.setPadding(padding5, padding, padding5, padding);
            return vw;
        }
    }

    void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        final Calendar date = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(ReservationActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, final int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                TimePickerDialog tpd = new TimePickerDialog(ReservationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
//                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
//                        SimpleDateFormat format1 = new SimpleDateFormat("MMM-dd hh:mm a");
                        SimpleDateFormat formatdate = new SimpleDateFormat("yyyy, MMM-dd");//hh:mm:ss a
                        SimpleDateFormat formattime = new SimpleDateFormat("hh:mm a");
                        Log.e("Date time format", formatdate.format(date.getTime()));
                        Log.e(" Dialog", "The choosen one " + date.getTime() + " , ");

                        datetv.setText(formatdate.format(date.getTime()));
                        timetv.setText(formattime.format(date.getTime()));
                        /*if (type.equalsIgnoreCase("from")) {
                            fromdate_ = date;
                            fromdatetextv.setText(format1.format(date.getTime()));
                            drawRouteMapping();
                        }
                        if (type.equalsIgnoreCase("to")) {
                            todate_ = date;
                            todatetextv.setText(format1.format(date.getTime()));
                            drawRouteMapping();
                        }*/
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);
                tpd.setCancelable(false);
                tpd.show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));

        dp.getDatePicker().setMaxDate(currentDate.getTimeInMillis() + (1296000000l));
        dp.getDatePicker().setMinDate(currentDate.getTimeInMillis());
        dp.setCancelable(false);
        dp.show();
    }

    void getBranchLocation() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ReservationActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(ReservationActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.ALL_BRANCHES), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        JSONObject jobj = new JSONObject(response);
                        if (jobj.optString("status").equalsIgnoreCase("1")) {
                            Log.e("response", response + "  forgot ");
                            JSONArray jarr = jobj.optJSONArray("data");
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject job = jarr.optJSONObject(i);
                                JSONArray arrJson = job.getJSONArray("payment_type");
                                String[] payment_type = new String[arrJson.length()];
                                for (int j = 0; j < arrJson.length(); j++) {
                                    payment_type[j] = arrJson.getString(j);
                                    Log.e("string arra ", payment_type[j] + " value");
                                }
//                                branchlist.add(new Branch(job.optString("id"), job.optString("name"),
//                                        job.optString("open"), job.optString("close"), job.optString("logo"), job.optString("address"), payment_type, job.optString("phone")));
                                branchlist.add(new Branch(job.optString("id"), job.optString("name"),
                                        job.optString("open_validation"), job.optString("close_validation"), job.optString("logo"), job.optString("address"), payment_type, job.optString("phone"),
                                        job.optString("open"), job.optString("close")));
                            }
                            branch.setAdapter(new Branchadap());
                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                        Log.e("data errr ", e.toString());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    alertDialog.cancel();
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        Log.e("res data", new String(response.data));
                        try {
                            JSONObject jobj = new JSONObject(new String(response.data));
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("error ", "parse err" + e.toString());
                        }
                    } else {
                        if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(ReservationActivity.this));
                    //  params.put("branch_id", "35");
                    Log.e("params ", params.toString());

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            sr.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Singleton.getIntance(ReservationActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void bookTable() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ReservationActivity.this);
            View av = getLayoutInflater().inflate(R.layout.progress_layout, null);
            builder.setView(av);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            RequestQueue queue = Volley.newRequestQueue(ReservationActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.RESERVATION), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        alertDialog.cancel();
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        JSONObject jobj = new JSONObject(response);
                        if (jobj.optString("status").equalsIgnoreCase("1")) {
                            Toast.makeText(ReservationActivity.this, "Reservation successful", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                        Log.e("data errr ", e.toString());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    alertDialog.cancel();
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        Log.e("res data", new String(response.data));
                        try {
                            JSONObject jobj = new JSONObject(new String(response.data));
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("error ", "parse err" + e.toString());
                        }
                    } else {
                        if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(ReservationActivity.this));
                    params.put("fname", str_firstname);
                    params.put("lname", str_lastname);
                    params.put("email", str_email);
                    params.put("branch", str_branch);
                    params.put("person", str_person);
                    params.put("mobile", str_mobileno);
                    params.put("time", str_time);
                    params.put("date", str_date);
                    params.put("user_id", apath.getUserid(ReservationActivity.this));

                    Log.e("params ", params.toString());
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            sr.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Singleton.getIntance(ReservationActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void getUserInfo() {
        try {
            RequestQueue queue = Volley.newRequestQueue(ReservationActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, apath.getMethodUrl(apath.USER), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        // msgResponse.setText(response.toString());
                        //                    // handleVolleyResponce(response);
                        Log.e("response", response + "  user info ");
                        JSONObject jobj = new JSONObject(response);
                        if (jobj.optString("status").equalsIgnoreCase("true")) {
                            JSONObject jo = jobj.optJSONObject("data");
                            String str_name = jo.optString("name");
                            String str_emailid = jo.optString("email");
                            String str_mobileno = jo.optString("phone");
                            String[] splited = str_name.split("\\s+");
                            String strfname="";
                            for (int i=0;i<splited.length;i++)
                            {
                                if (i > 0 && i == splited.length-1)
                                    lastname.setText(splited[i]);
                                else
                                  strfname=strfname+splited[i]+" ";
                            }
                            firstname.setText(strfname);

                            mobileno.setText(str_mobileno);
                            email.setText(str_emailid);
                        } else {
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                        Log.e("data errr ", e.toString());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        Log.e("res data", new String(response.data));
                        try {
                            JSONObject jobj = new JSONObject(new String(response.data));
                            Snackbar.make(mainlay, jobj.optString("message"), Snackbar.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("error ", "parse err" + e.toString());
                        }
                    } else {
                        if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.english)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_en), Snackbar.LENGTH_LONG).show();
                        else if (apath.getLanguage(ReservationActivity.this).equalsIgnoreCase(getString(R.string.arabic)))
                            Snackbar.make(mainlay, getResources().getString(R.string.oops_ar), Snackbar.LENGTH_LONG).show();

                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lang", apath.getLang(ReservationActivity.this));
                    params.put("action", "user_info");
                    params.put("user_id", apath.getUserid(ReservationActivity.this));
                    Log.e("params ", params.toString());

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            sr.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Singleton.getIntance(ReservationActivity.this).addToRequestQueue(sr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
