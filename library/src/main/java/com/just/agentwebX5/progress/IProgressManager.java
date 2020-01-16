package com.just.agentwebX5.progress;

/**
 *
 */

public interface IProgressManager<T extends IBaseProgressSpec> {
    T offer();
}
