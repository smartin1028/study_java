//
// WorkflowExecutor — 워크플로우 실행 엔진
//
// Java 비교: Service Layer 의 핵심 비즈니스 로직 클래스.
//
//   @Service
//   public class WorkflowExecutor {
//       private final LlmClient llmClient;
//
//       public List<ExecutionResult> executeWorkflow(List<Node> nodes, List<Edge> edges) {
//           List<ExecutionResult> results = new ArrayList<>();
//           for (Node node : nodes) {          // 순차 실행
//               results.add(executeNode(node));
//           }
//           return results;
//       }
//       ...
//   }
//
// React/TypeScript 에서의 Service:
//   - class 로 정의하고 new 로 인스턴스화 (싱글톤)
//   - export const instance = new Class(); 로 전역에서 동일 인스턴스 공유
//   - Java 의 @Service + @Autowired 와 달리 수동 싱글톤 관리
//

import { callLLM } from './llmClient';

//
// 데이터 구조체 (Java: record / DTO)
//
interface NodeData {
  id: string;
  label: string;
  agentType: string;
  prompt?: string;
  model?: string;
}

interface ExecutionResult {
  nodeId: string;
  success: boolean;
  output?: any;
  error?: string;
}

//
// export class → Java 의 public class 와 동일
//
export class WorkflowExecutor {
  //
  // 단일 노드 실행
  // Java: private ExecutionResult executeNode(NodeData node) { ... }
  //
  async executeNode(node: NodeData): Promise<ExecutionResult> {
    try {
      if (node.agentType === 'llm') {
        return await this.executeLLMNode(node);
      }

      // LLM 이외의 노드 타입은 즉시 성공 반환
      return {
        nodeId: node.id,
        success: true,
        output: { message: `${node.label} 노드 실행 완료` },
      };
    } catch (error: any) {
      return {
        nodeId: node.id,
        success: false,
        error: error.message,
      };
    }
  }

  //
  // private 메서드: LLM 노드 전용 실행 로직
  // Java: private ExecutionResult executeLLMNode(NodeData node) throws Exception { ... }
  //
  private async executeLLMNode(node: NodeData): Promise<ExecutionResult> {
    if (!node.prompt) {
      throw new Error('프롬프트가 설정되지 않았습니다.');
    }

    // 백엔드 /llm API 를 통해 LLM 추론 실행
    // Java: LlmResponse response = llmClient.call(messages, model);
    const data = await callLLM([{ role: 'user', content: node.prompt }], node.model);

    return {
      nodeId: node.id,
      success: true,
      output: data,
    };
  }

  //
  // 워크플로우 전체 실행 (현재는 순차 실행)
  // Java: public List<ExecutionResult> executeWorkflow(List<Node> nodes, List<Edge> edges) { ... }
  //
  // _edges 파라미터의 '_' 접두사: TypeScript 에서 "아직 사용하지 않는 변수" 관례.
  // Java: @SuppressWarnings("unused") 와 유사.
  //
  async executeWorkflow(
    nodes: any[],
    _edges: any[],
  ): Promise<ExecutionResult[]> {
    const results: ExecutionResult[] = [];

    // 순차 실행 (추후 DAG 기반 병렬 실행으로 확장 가능)
    // Java: for (Node n : nodes) { results.add(executeNode(toNodeData(n))); }
    for (const node of nodes) {
      const result = await this.executeNode({
        id: node.id,
        ...node.data,       // 스프레드 연산자: node.data 의 모든 속성을 풀어서 전달
      });
      results.push(result);
    }

    return results;
  }
}

//
// 싱글톤 인스턴스 export
// Java: @Service → Spring 이 싱글톤 Bean 으로 관리.
//       또는 public static final WorkflowExecutor INSTANCE = new WorkflowExecutor();
//
export const workflowExecutor = new WorkflowExecutor();
