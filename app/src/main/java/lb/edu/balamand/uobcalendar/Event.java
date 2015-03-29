package lb.edu.balamand.uobcalendar;

import java.io.Serializable;

/**
 * Created by Lenovo on 3/27/2015.
 */
public class Event implements Serializable {
    int _id;
    String _title;
    String _description;
    String _thumb;
    String _from_date;
    String _to_date;
    int _isHoliday;


    public Event() {

    }
    public Event(int _id, String _title, String _description, String _thumb, String _from_date, String _to_date, int _isHoliday) {
        this._id = _id;
        this._title = _title;
        this._description = _description;
        this._thumb = _thumb;
        this._from_date = _from_date;
        this._to_date = _to_date;
        this._isHoliday = _isHoliday;
    }

    public Event(String _title, String _description, String _thumb, String _from_date, String _to_date, int _isHoliday) {
        this._title = _title;
        this._description = _description;
        this._thumb = _thumb;
        this._from_date = _from_date;
        this._to_date = _to_date;
        this._isHoliday = _isHoliday;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public String get_description() {
        return _description;
    }

    public void set_description(String _description) {
        this._description = _description;
    }

    public String get_thumb() {
        return _thumb;
    }

    public void set_thumb(String _thumb) {
        this._thumb = _thumb;
    }

    public String get_from_date() {
        return _from_date;
    }

    public void set_from_date(String _from_date) {
        this._from_date = _from_date;
    }

    public String get_to_date() {
        return _to_date;
    }

    public void set_to_date(String _to_date) {
        this._to_date = _to_date;
    }

    public int get_isHoliday() {
        return _isHoliday;
    }

    public void set_isHoliday(int _isHoliday) {
        this._isHoliday = _isHoliday;
    }

    @Override
    public String toString() {
      return get_title() + "\n" + get_description();
    }
}
