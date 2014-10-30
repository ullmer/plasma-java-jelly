package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;
import com.oblong.util.logging.ObLog;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 7/10/13
 * Time: 1:33 PM
 */
public class StringField extends AbstractField<String> {

	private final ObLog log = ObLog.get(StringField.class);

	public StringField(String name) {
		this(null, false, name);
	}

	public StringField(SlawSchema schema, boolean isOptional, String name) {
		super(schema, isOptional, name);
	}

	@Override
	protected String fromSlaw_Custom(Slaw slaw) {
		try {
			return slaw.emitString();
		} catch (UnsupportedOperationException e) {
			log.w("Coercing slaw to string: " + slaw);
			return "" + slaw.emitBigInteger();

			/* Quickish workaround for "error-code" : !i64 -90025, in:
			Response protein received: !protein
			    ingests: !!omap
			    - !!string "error-code" : !i64 -90025
			    - !!string "description" : !!string "The file may not be a PDF."
			    - !!string "summary" : !!string "Could Not Import PDF"
			    - !!string "file-id" : !i32 1
			    descrips: !!seq
			    - !!string "mezzanine"
			    - !!string "prot-spec v2.8"
			    - !!string "response"
			    - !!string "asset-upload-pdf-ready"
			    - !!string "from:"
			    - !!string "native-mezz"
			    - !!string "to:"
			    - !!seq
			    - !!string "android-ab6caad9-29b5-4e33-b77f-38c10d44a9fb"
			    - !i32 72
			    - !!string "error"
			    Index: 1050085, Stamp: 1.414697445125685E9
			*/
		}
	}

	@Override
	protected Slaw toSlaw_Custom(String value) {
		return Slaw.string(value);
	}
}
