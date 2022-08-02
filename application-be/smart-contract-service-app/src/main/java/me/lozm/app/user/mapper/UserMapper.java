package me.lozm.app.user.mapper;

import me.lozm.app.user.dto.UserSignUpDto;
import me.lozm.app.user.vo.UserSignUpVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserSignUpVo.Request toSignUpVo(UserSignUpDto.SignUpRequest requestDto);

    UserSignUpDto.SignUpResponse toSignUpDto(UserSignUpVo.Response responseVo);
}
