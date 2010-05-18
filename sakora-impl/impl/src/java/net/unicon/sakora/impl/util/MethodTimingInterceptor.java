/*
 * Licensed to the Sakai Foundation under one or more contributor
 * license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.
 * The Sakai Foundation licenses this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.unicon.sakora.impl.util;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MethodTimingInterceptor implements MethodInterceptor
{
    private static final Log logger = LogFactory.getLog(MethodTimingInterceptor.class);
    private boolean logMethodTime = false;
    /**
     * @param logMethodTime the logMethodTime to set
     */
    public void setLogMethodTime(boolean logMethodTime)
    {
        this.logMethodTime = logMethodTime;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        if (!logMethodTime)
        {
            return invocation.proceed();
        }
        String targetName = invocation.getThis().getClass().getName();
        String methodName = invocation.getMethod().getName();
        Class<?> clazzes[] = invocation.getMethod().getParameterTypes();
        StringBuffer args = new StringBuffer();
        int i =0;
        for (Class<?> clazz : clazzes) {
			args.append(clazz.getName());
			if (++i < clazzes.length) args.append(",");
		}
        long start = System.nanoTime();
        Object result = invocation.proceed();
        long end = System.nanoTime();
        if (logger.isDebugEnabled())
        {
            logger.debug("execution of method \t"+methodName+"("+ args + ")\t"+targetName+"\t"+(end-start)+"\tnanoseconds");
        }

        return result;
    }

}
