package ru.mos.elk.netframework.model.results;

import android.text.TextUtils;

import ru.mos.elk.R;
import ru.mos.elk.netframework.adapters.ButtonHolder;
import ru.mos.elk.netframework.adapters.Holder;
import ru.mos.elk.netframework.adapters.ImgHolder;
import ru.mos.elk.netframework.adapters.RichLinkHolder;
import ru.mos.elk.netframework.adapters.RichTextHolder;
import ru.mos.elk.netframework.adapters.TableLinkHolder;
import ru.mos.elk.netframework.adapters.TableTextHolder;
import ru.mos.elk.netframework.adapters.TextHolder;
import ru.mos.elk.netframework.adapters.TitleHolder;
import ru.mos.elk.netframework.adapters.UnsupportedHolder;

/**
 * @author Александр Свиридов
 * 
 */
public enum ResultType {
	TEXT {
		@Override
		public ResultText getResult(String style) {
			return new ResultText(style);
		}

		@Override
		public int getLayout() {
			return R.layout.item_dynamics_text;
		}

		@Override
		public TextHolder getHolder() {
			return new TextHolder();
		}
	},
	LINK {
		@Override
		public ResultLink getResult(String style) {
			return new ResultLink(style);
		}

		@Override
		public int getLayout() {
			return R.layout.item_dynamics_link;
		}

		@Override
		public TitleHolder<ResultLink> getHolder() {
			return new TitleHolder<ResultLink>();
		}
	},
	RICH_LINK {
		@Override
		public ResultRichLink getResult(String style) {
			return new ResultRichLink(style);
		}

		@Override
		public int getLayout() {
			return R.layout.item_dynamics_richlink;
		}

		@Override
		public RichLinkHolder<ResultRichLink> getHolder() {
			return new RichLinkHolder<ResultRichLink>();
		}
	},
    RICH_TEXT {
        @Override
        public ResultRichText getResult(String style) {
            return new ResultRichText(style);
        }

        @Override
        public int getLayout() {
            return R.layout.item_dynamics_richlink;
        }

        @Override
        public RichTextHolder<ResultRichText> getHolder() {
            return new RichTextHolder<ResultRichText>();
        }
    },
    TABLE_LINK {
        @Override
        public ResultTableLink getResult(String style) {
            return new ResultTableLink(style);
        }

        @Override
        public int getLayout() {
            return R.layout.item_dynamics_tablelink;
        }

        @Override
        public TableLinkHolder getHolder() {
            return new TableLinkHolder();
        }


    },
    TABLE_TEXT {
        @Override
        public ResultTableText getResult(String style) {
            return new ResultTableText(style);
        }

        @Override
        public int getLayout() {
            return R.layout.item_dynamics_tabletext;
        }

        @Override
        public TableTextHolder getHolder() {
            return new TableTextHolder();
        }
    },
	SEPARATOR {
		@Override
		public ResultTitle getResult(String style) {
			return new ResultTitle(ResultType.SEPARATOR, style);
		}

		@Override
		public int getLayout() {
			return R.layout.item_dynamics_separator;
		}

		@Override
		public TitleHolder<ResultSeparator> getHolder() {
			return new TitleHolder<ResultSeparator>();
		}
	},
	IMG {
		@Override
		public ResultImg getResult(String style) {
			return new ResultImg(style);
		}

		@Override
		public int getLayout() {
			return R.layout.item_dynamics_img;
		}

		@Override
		public ImgHolder<ResultImg> getHolder() {
			return new ImgHolder<ResultImg>();
		}
	},
	TITLE {
		@Override
		public ResultTitle getResult(String style) {
			return new ResultTitle(style);
		}

		@Override
		public int getLayout() {
			return R.layout.item_dynamics_title;
		}

		@Override
		public TitleHolder<ResultTitle> getHolder() {
			return new TitleHolder<ResultTitle>();
		}
	},
	PHONE {
		@Override
		public ResultText getResult(String style) {
			return new ResultText(ResultType.PHONE,style);
		}

		@Override
		public int getLayout() {
			return R.layout.item_dynamics_phone;
		}

		@Override
		public TextHolder getHolder() {
			return new TextHolder();
		}
	},
	MAPPOINT {
		@Override
		public ResultMapPoint getResult(String style) {
			return new ResultMapPoint(style);
		}

		@Override
		public int getLayout() {
			return R.layout.item_dynamics_mappoint;
		}

		@Override
		public TextHolder getHolder() {
			return new TextHolder();
		}
	},
	SEARCH {
		@Override
		public ResultSearch getResult(String style) {
			return new ResultSearch(style);
		}

		@Override
		public int getLayout() {
			return R.layout.item_dynamics_unsupported;
		}

		@Override
		public UnsupportedHolder getHolder() {
			return new UnsupportedHolder();
		}
	},
	APPBUTTON {
		@Override
		public ResultAppButton getResult(String style) {
			return new ResultAppButton(style);
		}

		@Override
		public int getLayout() {
			return R.layout.item_dynamics_unsupported;
		}

		@Override
		public UnsupportedHolder getHolder() {
			return new UnsupportedHolder();
		}
	},
	BUTTON {

		@Override
		public ResultButton getResult(String style) {
			return new ResultButton(style);
		}

		@Override
		public int getLayout() {
			return R.layout.item_dynamics_button;
		}

		@Override
		public ButtonHolder getHolder() {
			return new ButtonHolder();
		}
	},
	OUR_APP{

		@Override
		public ResultOurApp getResult(String style) {
			return new ResultOurApp(style);
		}

		@Override
		public int getLayout() {
			return R.layout.item_dynamics_our_app;
		}

		@Override
		public RichTextHolder<ResultOurApp> getHolder() {
			return new RichTextHolder<ResultOurApp>();
		}
		
	},
    WWW{
        @Override
        public ResultText getResult(String style) {
            return new ResultText(ResultType.WWW,style);
        }

        @Override
        public int getLayout() {
            return R.layout.item_dynamics_www;
        }

        @Override
        public TextHolder getHolder() {
            return new TextHolder();
        }
    },
    FILE{
        @Override
        public ResultFile getResult(String style) {
            return new ResultFile(style);
        }

        @Override
        public int getLayout() {
            return R.layout.item_dynamics_file;
        }

        @Override
        public TitleHolder<ResultFile> getHolder() {
            return new TitleHolder<ResultFile>();
        }
    };

	public abstract Result getResult(String style);
	public abstract int getLayout();
	public abstract Holder<? extends Result> getHolder();

	public static ResultType safeValueOf(String name) {
		if (!TextUtils.isEmpty(name)) {
			String upName = name.toUpperCase();
			for (ResultType type : ResultType.values())
				if (upName.equals(type.name()))
					return type;
		}

		return null;
	}

}
