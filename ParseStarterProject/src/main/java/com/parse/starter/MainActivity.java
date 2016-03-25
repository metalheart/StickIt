/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseAnalytics;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    public class ToDoItem {

        boolean done;
        String description;


        ToDoItem(boolean _done, String _description) {
            done = _done;
            description = _description;
        }
    }


    public class ToDoItemAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater lInflater;
        public ArrayList<ToDoItem> objects;

        ToDoItemAdapter(Context context, ArrayList<ToDoItem> products) {
            ctx = context;
            objects = products;
            lInflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // кол-во элементов
        @Override
        public int getCount() {
            return objects.size();
        }

        // элемент по позиции
        @Override
        public Object getItem(int position) {
            return objects.get(position);
        }

        // id по позиции
        @Override
        public long getItemId(int position) {
            return position;
        }

        // пункт списка
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // используем созданные, но не используемые view
            View view = convertView;
            if (view == null) {
                view = lInflater.inflate(R.layout.todo_item, parent, false);
            }

            ToDoItem p = getToDoItem(position);

            // заполняем View в пункте списка данными из товаров: наименование, цена
            // и картинка
            ((CheckBox) view.findViewById(R.id.cbBox)).setChecked(p.done);
            ((TextView) view.findViewById(R.id.tvDescr)).setText(p.description);

            CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);
            // присваиваем чекбоксу обработчик
            cbBuy.setOnCheckedChangeListener(myCheckChangList);
            // пишем позицию
            cbBuy.setTag(position);

            return view;
        }

        // товар по позиции
        ToDoItem getToDoItem(int position) {
            return ((ToDoItem) getItem(position));
        }

        // обработчик для чекбоксов
        CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // меняем данные товара (в корзине или нет)
                //getToDoItem((Integer) buttonView.getTag()).box = isChecked;
            }
        };
    }

    private ToDoItemAdapter todoItemsAdapter = null;
    private ListView lvMain = null;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      //ParseObject.registerSubclass(ToDoItem);
      todoItemsAdapter = new ToDoItemAdapter(this, new ArrayList<ToDoItem>());
      lvMain = (ListView) findViewById(R.id.listView);
      lvMain.setAdapter(todoItemsAdapter);

        update();
      ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

    private void update()
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TODO");
        query.getQuery("TODO");
        query.findInBackground(new FindCallback<ParseObject>() {
                                   public void done(List<ParseObject> objects, ParseException e) {
                                       if (e == null) {
                                           fillTODOs(objects);
                                       } else {
                                           Log.d("MyApp", "Failed to retrieve todos...");
                                       }
                                   }
                               }
        );
    }

    private void fillTODOs(List<ParseObject> objects) {
        ArrayList<ToDoItem> todoItems = new ArrayList<ToDoItem>();
        for (ParseObject p : objects)
        {
            todoItems.add(new ToDoItem(p.getBoolean("done"), p.getString("description")));
        }
        todoItemsAdapter.objects = todoItems;
        lvMain.invalidate();
    }
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.add_todo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add TODO!");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                ParseObject gameScore = new ParseObject("TODO");
                gameScore.put("done", false);
                gameScore.put("description", text);
                gameScore.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        update();
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
