/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.oauth2;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Test methods for {@link AbstractOAuth2ApiBinding}, to verify the extension point where it's embedded
 * {@link RestTemplate} can be manipulated, such as wrapping it inside a AOP proxy.
 *
 * @author Greg Turnquist
 */
public class AbstractOAuth2ApiBindingTest {

	@Test
	public void testOriginalSocialTemplateToProveBackwardsCompatibility() throws Exception {
		MySocialTemplate template = new MySocialTemplate("some acces code");
		template.afterPropertiesSet();
		RestTemplate restTemplate = template.getRestTemplate();

		assertThat(ClassUtils.isCglibProxy(restTemplate), is(false));
		assertThat(template.getState(), equalTo("no state here"));
		assertThat(ClassUtils.isCglibProxy(template.getSubSocialTemplate().getRestTemplate()), is(false));
	}

	@Test
	public void testAugmentedSocialTemplate() throws Exception {
		AugmentedSocialTemplate template = new AugmentedSocialTemplate("some access code", "key piece of data");
		template.afterPropertiesSet();
		RestTemplate restTemplate = template.getRestTemplate();
		restTemplate.getMessageConverters();

		assertThat(ClassUtils.isCglibProxy(restTemplate), is(true));
		assertThat(template.getState(), equalTo("This template was touched by an aspect"));
		assertThat(ClassUtils.isCglibProxy(template.getSubSocialTemplate().getRestTemplate()), is(true));
	}

	/**
	 * Imaginary social service that extends the {@link AbstractOAuth2ApiBinding}. Used to
	 * demonstrate default behavior of pass through on {@link RestTemplate}.
	 * NOTE: This version is patterned like many Spring Social providers that "initSubApis()"
	 * in the constructor call. Test up above show that backwards compatibility is NOT broken.
	 */
	private static class MySocialTemplate extends AbstractOAuth2ApiBinding {

		private String state = "no state here";
		private MySubSocialTemplate subSocialTemplate;

		public MySocialTemplate(String accessToken) {
			super(accessToken);
			initSubApis();
		}

		private void initSubApis() {
			this.subSocialTemplate = new MySubSocialTemplate(getRestTemplate());
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public MySubSocialTemplate getSubSocialTemplate() {
			return subSocialTemplate;
		}

	}

	/**
	 * Imaginary social service that extends the {@link AbstractOAuth2ApiBinding}. Used to
	 * demonstrate default behavior of pass through on {@link RestTemplate}.
	 * NOTE: This version moves initSubApis() into the new configuration hook to make it more
	 * extensible.
	 */
	private static class MyRefactoredSocialTemplate extends AbstractOAuth2ApiBinding {

		private String state = "no state here";
		private MySubSocialTemplate subSocialTemplate;

		public MyRefactoredSocialTemplate(String accessToken) {
			super(accessToken);
		}

		private void initSubApis() {
			this.subSocialTemplate = new MySubSocialTemplate(getRestTemplate());
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public MySubSocialTemplate getSubSocialTemplate() {
			return subSocialTemplate;
		}

		@Override
		protected void postConstructionConfiguration() {
			initSubApis();
		}
	}
	/**
	 * Sample class that represent a subset of operations as commonly seen in Spring Social providers.
	 */
	private static class MySubSocialTemplate {

		private final RestTemplate restTemplate;

		public MySubSocialTemplate(RestTemplate restTemplate) {
			this.restTemplate = restTemplate;
		}

		public RestTemplate getRestTemplate() {
			return restTemplate;
		}
	}

	/**
	 * Extension of the imaginary soclail service. Uses Spring Social's extension point to wrap the embedded
	 * {@link RestTemplate} with some advice.
	 */
	private static class AugmentedSocialTemplate extends MyRefactoredSocialTemplate {

		private final String keyPieceOfDataForAugmentation;

		public AugmentedSocialTemplate(String accessToken, String keyPieceOfDataForAugmentation) {
			super(accessToken);
			this.keyPieceOfDataForAugmentation = keyPieceOfDataForAugmentation;
		}

		@Override
		protected RestTemplate postProcess(RestTemplate restTemplate) {
			AspectJProxyFactory factory = new AspectJProxyFactory(restTemplate);
			factory.addAspect(new RestTemplateAdvice(this, this.keyPieceOfDataForAugmentation));
			factory.setProxyTargetClass(true);
			return factory.getProxy();
		}

	}

	/**
	 * Some Spring AOP advice used to prove ability to wrap embedded {@link }
	 */
	@Aspect
	public static class RestTemplateAdvice {

		private final MyRefactoredSocialTemplate template;
		private final String keyPieceOfDataForAugmentation;

		public RestTemplateAdvice(MyRefactoredSocialTemplate template, String keyPieceOfDataForAugmentation) {
			this.template = template;
			this.keyPieceOfDataForAugmentation = keyPieceOfDataForAugmentation;
		}

		@Around("execution(* org.springframework.web.client.RestTemplate.*(..))")
		public Object around(ProceedingJoinPoint point) throws Throwable {
			template.setState("This template was touched by an aspect");
			assertThat(this.keyPieceOfDataForAugmentation, is(notNullValue()));
			return point.proceed();
		}
	}

}