package org.osehra.cpe.datetime.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.osehra.cpe.datetime.IntervalOfTime;
import org.osehra.cpe.datetime.PointInTime;

public class HealthTimeModule extends SimpleModule {
    public HealthTimeModule() {
        super("HealthTimeModule", ModuleVersion.instance.version());

        addDeserializer(PointInTime.class, new PointInTimeDeserializer());
        addDeserializer(IntervalOfTime.class, new IntervalOfTimeDeserializer());

        addSerializer(PointInTime.class, new PointInTimeSerializer());
        addSerializer(IntervalOfTime.class, new IntervalOfTimeSerializer());
    }
}
