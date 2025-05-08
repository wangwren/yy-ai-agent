package com.core.aiagent.advisor;


import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisorChain;
import org.springframework.ai.chat.model.MessageAggregator;


/**
 * 自定义日志拦截器
 * 实现自己的advisor，打印ai请求前后的日志
 * 不需要交给spring管理，不用@Component
 */
@Slf4j
public class MyLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public int getOrder() {
		return 0;
	}

	private AdvisedRequest before(AdvisedRequest request) {
		log.info("AI request: {}", request.userText());
		return request;
	}

	private void observeAfter(AdvisedResponse advisedResponse) {
		log.info("AI response: {}", advisedResponse.response().getResult().getOutput().getText());
	}

	/**
	 * 对于非流式处理 CallAroundAdvisor
	 */
	@Override
	public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {

		// 前置处理请求
		advisedRequest = before(advisedRequest);

		// 调用链中的下一个advisor
		AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);

		// 后置处理响应
		observeAfter(advisedResponse);

		return advisedResponse;
	}

	/**
	 * 对于流式处理 StreamAroundAdvisor
	 */
	@Override
	public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {

		advisedRequest = before(advisedRequest);

		Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);

		return new MessageAggregator().aggregateAdvisedResponse(advisedResponses, this::observeAfter);
	}

}
