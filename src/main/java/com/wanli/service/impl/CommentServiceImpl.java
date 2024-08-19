package com.wanli.service.impl;

import com.wanli.entity.Comment;
import com.wanli.mapper.CommentMapper;
import com.wanli.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Troye
 * @since 2024-06-22
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
