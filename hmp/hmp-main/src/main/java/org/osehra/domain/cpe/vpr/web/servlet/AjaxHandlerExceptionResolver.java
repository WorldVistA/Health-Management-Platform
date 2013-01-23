package org.osehra.cpe.vpr.web.servlet;

import org.osehra.cpe.auth.AuthController;
import org.osehra.cpe.jsonc.JsonCResponse;
import org.osehra.cpe.vpr.NotFoundException;
import org.osehra.cpe.vpr.web.WebUtils;
import org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView;

public class AjaxHandlerExceptionResolver extends DefaultHandlerExceptionResolver {

    private static Logger log = LoggerFactory.getLogger(AuthController.class);
    
    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (WebUtils.isAjax(request)) {
            ModelAndView modelAndView = super.doResolveException(request, response, handler, ex);
            if (modelAndView == null) {
                if (ex instanceof NotFoundException) {
                    modelAndView = handleNotFound((NotFoundException) ex, request, response);
                } else {
                    modelAndView = handleInternalServerError(ex, request, response);
                }
            }
            return modelAndView;
        }
        return null;
    }

    protected ModelAndView handleInternalServerError(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return contentNegotiatingModelAndView(createErrorResponse(request, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex));
    }

    protected ModelAndView handleNotFound(NotFoundException e, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return contentNegotiatingModelAndView(createErrorResponse(request, HttpServletResponse.SC_NOT_FOUND, e));
    }

    @Override
    protected ModelAndView handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        super.handleHttpRequestMethodNotSupported(ex, request, response, handler);
        return contentNegotiatingModelAndView(createErrorResponse(request, HttpServletResponse.SC_METHOD_NOT_ALLOWED, ex));
    }

    @Override
    protected ModelAndView handleNoSuchRequestHandlingMethod(NoSuchRequestHandlingMethodException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        super.handleNoSuchRequestHandlingMethod(ex, request, response, handler);
        return contentNegotiatingModelAndView(createErrorResponse(request, HttpServletResponse.SC_NOT_FOUND, ex));
    }

    @Override
    protected ModelAndView handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        super.handleHttpMediaTypeNotSupported(ex, request, response, handler);
        return contentNegotiatingModelAndView(createErrorResponse(request, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, ex));
    }

    @Override
    protected ModelAndView handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        super.handleHttpMediaTypeNotAcceptable(ex, request, response, handler);
        return contentNegotiatingModelAndView(createErrorResponse(request, HttpServletResponse.SC_NOT_ACCEPTABLE, ex));
    }

    @Override
    protected ModelAndView handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        super.handleMissingServletRequestParameter(ex, request, response, handler);
        return contentNegotiatingModelAndView(createErrorResponse(request, HttpServletResponse.SC_BAD_REQUEST, ex));
    }

    @Override
    protected ModelAndView handleServletRequestBindingException(ServletRequestBindingException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        super.handleServletRequestBindingException(ex, request, response, handler);
        return contentNegotiatingModelAndView(createErrorResponse(request, HttpServletResponse.SC_BAD_REQUEST, ex));
    }

    @Override
    protected ModelAndView handleConversionNotSupported(ConversionNotSupportedException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        super.handleConversionNotSupported(ex, request, response, handler);
        return contentNegotiatingModelAndView(createErrorResponse(request, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex));
    }

    @Override
    protected ModelAndView handleTypeMismatch(TypeMismatchException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        super.handleTypeMismatch(ex, request, response, handler);
        return contentNegotiatingModelAndView(createErrorResponse(request, HttpServletResponse.SC_BAD_REQUEST, ex));
    }

    @Override
    protected ModelAndView handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        super.handleHttpMessageNotReadable(ex, request, response, handler);
        return contentNegotiatingModelAndView(createErrorResponse(request, HttpServletResponse.SC_BAD_REQUEST, ex));
    }

    @Override
    protected ModelAndView handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        super.handleHttpMessageNotWritable(ex, request, response, handler);
        return contentNegotiatingModelAndView(createErrorResponse(request, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex));
    }

    @Override
    protected ModelAndView handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        super.handleMethodArgumentNotValidException(ex, request, response, handler);
        return contentNegotiatingModelAndView(createErrorResponse(request, HttpServletResponse.SC_BAD_REQUEST, ex));
    }

    @Override
    protected ModelAndView handleMissingServletRequestPartException(MissingServletRequestPartException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        super.handleMissingServletRequestPartException(ex, request, response, handler);
        return contentNegotiatingModelAndView(createErrorResponse(request, HttpServletResponse.SC_BAD_REQUEST, ex));
    }

    protected JsonCResponse createErrorResponse(HttpServletRequest request, int statusCode, Exception ex) {
    	log.error(ex.getMessage(), ex);
    	// 
        return JsonCResponse.create(request).setError(Integer.toString(statusCode), ex);
    }
}
