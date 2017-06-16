package com.cacheserverdeploy.deploy;

import java.util.LinkedList;
import java.util.List;

/**
 * 依据每条路径上的流量计算路径
 * 		需要输入一个二维的邻接矩阵存储已知的流量信息
 * @author wsk
 *
 */
public class FindPath {
	
	private int[][] flow;		// 存储每条路径上计算出来的流量
	private int[] server;		// 存储服务器的部署方案
	private int[] cms;			// 存储消费节点位置
	private int[] need;			// 每个消费节点的需求
	private LinkedList<List<Integer>> list = new LinkedList<>();	// 所有路径
	
	public FindPath(int[][] flow,int[] server,int[] cms,int[] need){
		this.flow=flow;
		this.server=server;
		this.cms = cms;
		this.need=need;
	}
	
	
	
	public LinkedList<List<Integer>> path(){
		for(int i=0;i<cms.length;i++){
			while(need[i]>0){
				findPath(cms[i],i);
			}
		}
		return list;
	}
	
	
	
	
	/**
	 * 寻找消费节点c的其中一条路径
	 * @param c	消费节点部署位置
	 * 
	 */
	public void findPath(int c,int n){
		for(int i=0;i<flow.length;i++){
			if(flow[i][c]>0){
				LinkedList<Integer> stack = new LinkedList<>();
				stack.add(c);
				stack.add(i);
				
				// 变量min存储这条路径上的流量，受最小流量路径约束
				int min = flow[i][c];
				int k=i;
				// 判断k节点是否部署服务器
				while(!isServer(k)){
					// k节点没有部署服务器,寻找k节点的流量来源
					for(int t=0;t<flow.length;t++){
						if(flow[t][k]>0){
							// 找到k节点流量来自t节点,t节点入栈
							stack.add(t);
							// 跟新路径上的流量值
							if(min>flow[t][k])
								min=flow[t][k];
							// 将k节点值更新为t节点
							k=t;
							// 找到流量来源， 跳出寻找流量来源循环
							break;
						}
					}
				}
				
				/*
				 *  1条路径寻找结束，服务器部署在k节点，路径上流量为min,
				 */
				list.add(stack);
				if(min>=need[n]){
					// 如果找到路径上的流量值大于节点需要的流量值,更新所有链路的流量值
					for(int j=stack.size()-1;j>0;j--){
						flow[stack.get(j)][stack.get(j-1)] = flow[stack.get(j)][stack.get(j-1)] - need[n];
					}
					stack.add(need[n]);
					// 更新当前消费节点需求
					need[n]=0;
					return;
					
				} else {
					for(int j=stack.size()-1;j>0;j--){
						flow[stack.get(j)][stack.get(j-1)] = flow[stack.get(j)][stack.get(j-1)] - min;
					}
					stack.add(min);
					// 更新当前消费节点需求
					need[n] = need[n]-min;
					return;
				}
				
			}
		}
	}
	
	
	
	public boolean isServer(int e){
		for(int i=0;i<server.length;i++){
			if(e==server[i])
				return true;
		}
		return false;
	}
	
}
