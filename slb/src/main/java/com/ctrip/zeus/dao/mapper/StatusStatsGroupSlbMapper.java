package com.ctrip.zeus.dao.mapper;

import com.ctrip.zeus.dao.entity.StatusStatsGroupSlb;
import com.ctrip.zeus.dao.entity.StatusStatsGroupSlbExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StatusStatsGroupSlbMapper {
    long countByExample(StatusStatsGroupSlbExample example);

    int deleteByExample(StatusStatsGroupSlbExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StatusStatsGroupSlb record);

    int insertSelective(StatusStatsGroupSlb record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table status_stats_group_slb
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    StatusStatsGroupSlb selectOneByExample(StatusStatsGroupSlbExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table status_stats_group_slb
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    StatusStatsGroupSlb selectOneByExampleSelective(@Param("example") StatusStatsGroupSlbExample example, @Param("selective") StatusStatsGroupSlb.Column... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table status_stats_group_slb
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<StatusStatsGroupSlb> selectByExampleSelective(@Param("example") StatusStatsGroupSlbExample example, @Param("selective") StatusStatsGroupSlb.Column... selective);

    List<StatusStatsGroupSlb> selectByExample(StatusStatsGroupSlbExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table status_stats_group_slb
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    StatusStatsGroupSlb selectByPrimaryKeySelective(@Param("id") Long id, @Param("selective") StatusStatsGroupSlb.Column... selective);

    StatusStatsGroupSlb selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StatusStatsGroupSlb record, @Param("example") StatusStatsGroupSlbExample example);

    int updateByExample(@Param("record") StatusStatsGroupSlb record, @Param("example") StatusStatsGroupSlbExample example);

    int updateByPrimaryKeySelective(StatusStatsGroupSlb record);

    int updateByPrimaryKey(StatusStatsGroupSlb record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table status_stats_group_slb
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int upsert(StatusStatsGroupSlb record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table status_stats_group_slb
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int upsertSelective(StatusStatsGroupSlb record);

    /*Self defined*/
    int batchUpdateStatus(List<StatusStatsGroupSlb> records);

    int batchInsert(List<StatusStatsGroupSlb> records);

    List<Map<Long, Integer>> countBySlbAndStatus(@Param("slbIds") List<Long> slbIds, @Param("valStatus") Integer valStatus);
}