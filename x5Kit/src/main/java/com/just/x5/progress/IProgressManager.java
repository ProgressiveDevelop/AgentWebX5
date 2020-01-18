package com.just.x5.progress;

/**
 *
 */

public interface IProgressManager<T extends IBaseProgressSpec> {
    T offer();
}
