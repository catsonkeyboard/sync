package org.catsonkeyboard.entities;

import jakarta.persistence.MappedSuperclass;
import org.catsonkeyboard.annotation.SystemField;

@MappedSuperclass
public abstract class BaseModel {

    @SystemField
    private Boolean _del;

    @SystemField
    private Long _lut;

    public Boolean get_del() {
        return _del;
    }

    public void set_del(Boolean _del) {
        this._del = _del;
    }

    public Long get_lut() {
        return _lut;
    }

    public void set_lut(Long _lut) {
        this._lut = _lut;
    }
}
