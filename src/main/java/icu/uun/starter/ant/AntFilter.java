package icu.uun.starter.ant;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.alibaba.fastjson.JSON;

/**
 * @author qiushengming
 */
public class AntFilter extends Filter<ILoggingEvent> {
    private String match;
    Level level;

    public AntFilter() {
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (this.match == null) {
            return FilterReply.DENY;
        } else {
            this.level = this.level == null ? Level.INFO : this.level;
            if (!event.getLevel().isGreaterOrEqual(this.level)) {
                return FilterReply.DENY;
            } else if ("{}".equals(event.getMessage()) && event.getArgumentArray().length >= 1) {
                return JSON.toJSONString(event.getArgumentArray()[0]).contains(this.match) ? FilterReply.NEUTRAL : FilterReply.DENY;
            } else {
                return FilterReply.DENY;
            }
        }
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}